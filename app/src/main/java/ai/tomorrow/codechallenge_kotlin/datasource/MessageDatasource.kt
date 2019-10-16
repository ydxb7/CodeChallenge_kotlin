package ai.tomorrow.codechallenge_kotlin.datasource

import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
import ai.tomorrow.codechallenge_kotlin.model.User
import ai.tomorrow.codechallenge_kotlin.model.getDatabase
import ai.tomorrow.codechallenge_kotlin.utils.DownloadCallback
import android.app.Application
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.JsonReader
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


const val CODE_CHALLENGE_URL = "https://codechallenge.secrethouse.party/"
//const val MASSAGE_NUM = 200
//const val CODE_CHALLENGE_URL = "https://www.google.com"

class MessageDatasource(application: Application) {

    private val TAG = "MessageDatasource"
    private val mutex = Mutex()

    val database = getDatabase(application).messageDao

    val messages = database.getAllMessages()

    fun clearAllMessages() = database.clear()

    fun insertAllMessages(vararg m: DatabaseMessage) = database.insertAll(*m)

    private var mDownloadTask: DownloadTask? = null

    fun fetchFromNet(massageNum: Int, callback: DownloadCallback) {
        cancelDownload()
        mDownloadTask = DownloadTask(massageNum, callback)
        (mDownloadTask as DownloadTask).execute(CODE_CHALLENGE_URL)
    }

    fun cancelDownload() {
        if (mDownloadTask != null) {
            (mDownloadTask as DownloadTask).cancel(true)
            mDownloadTask = null
        }
    }

    suspend fun fetchMessagesFromNet(urlString: String, messageNum: Int) {
        val url = URL(urlString)
        withContext(Dispatchers.IO) {
            val resultMessages = downloadUrl(url, messageNum)
        }


    }


    @Throws(IOException::class)
    private fun downloadUrl(url: URL, messageNum: Int): List<DatabaseMessage>? {
        var stream: InputStream? = null
        var connection: HttpsURLConnection? = null
        val messages = ArrayList<DatabaseMessage>()
        var num = 0
        try {
            connection = url.openConnection() as HttpsURLConnection
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.readTimeout = 3000
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.connectTimeout = 3000
            // For this use case, set HTTP method to GET.
            connection.requestMethod = "GET"
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.doInput = true
            // Open communications link (network traffic occurs here).
            connection.connect()
//                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS)
            val responseCode = connection.responseCode
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw IOException("HTTP error code: $responseCode")
            }
            // Retrieve the response body as an InputStream.
            stream = connection.inputStream
//                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0)
            if (stream != null) {
                val reader = JsonReader(InputStreamReader(stream, "UTF-8"))
                reader.isLenient = true
                try {
                    while (reader.hasNext() && num++ < messageNum) {
                        val message = readMessage(reader)
                        if (message != null) {
                            messages.add(message)
                        }
                        if (num % 100 == 0) {
                            insertAllMessages(*(messages.toTypedArray()))
                            messages.clear()
                        }
                        Log.d(TAG, "messages.size = ${messages.size}")
                        Log.d(TAG, "messages = $message")
                    }
                    insertAllMessages(*(messages.toTypedArray()))
                } catch (e: IOException) {
                    Log.e(TAG, "Problem reading messages objects")
                } finally {
                    reader.close()
                }
//                    publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0)
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            stream?.close()
            connection?.disconnect()
        }
        return messages
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader): DatabaseMessage? {
        var toId = ""
        var toName = ""
        var fromId = ""
        var fromName = ""
        var timestamp = 0L
        var areFriends = false

        reader.beginObject()
        try {
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "to") {
                    val toUser = readUser(reader)
                    if (toUser == null) {
                        reader.skipValue()
                    } else {
                        toId = toUser.id
                        toName = toUser.name
                    }
                } else if (name == "from") {
                    val fromUser = readUser(reader)
                    if (fromUser == null) {
                        reader.skipValue()
                    } else {
                        fromId = fromUser.id
                        fromName = fromUser.name
                    }
                } else if (name == "timestamp") {
                    timestamp = reader.nextLong()
                } else if (name == "areFriends") {
                    areFriends = reader.nextBoolean()
                } else {
                    reader.skipValue()
                }
            }
        } catch (e: IOException) {
            reader.endObject()
            return null
        } finally {
            reader.endObject()
            return DatabaseMessage(toId, toName, fromId, fromName, timestamp, areFriends)
        }
    }

    @Throws(IOException::class)
    fun readUser(reader: JsonReader): User? {
        var userName = ""
        var userId = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "name") {
                userName = reader.nextString()
            } else if (name == "id") {
                userId = reader.nextString()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
        return User(userId, userName)
    }

    private inner class DownloadTask(val massageNum: Int, val mCallback: DownloadCallback) :
        AsyncTask<String, Void?, Void?>() {


        override fun onPreExecute() {
            if (mCallback != null) {
                val networkInfo = mCallback.getActiveNetworkInfo()
                if (networkInfo == null || !networkInfo!!.isConnected() ||
                    networkInfo!!.getType() != ConnectivityManager.TYPE_WIFI && networkInfo!!.getType() != ConnectivityManager.TYPE_MOBILE
                ) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback.updateFromDownload(null)
                    cancel(true)
                }
            }
        }

        override fun doInBackground(vararg urls: String): Void? {
            if (!isCancelled && urls != null && urls.size > 0) {
                val urlString = urls[0]
                val url = URL(urlString)
                clearAllMessages()
                val resultMessages = downloadUrl(url)
                if (resultMessages != null) {
                    insertAllMessages(*(resultMessages.toTypedArray()))
                }
            }
            return null
        }

//        override fun onProgressUpdate(vararg values: List<DatabaseMessage>?) {
//            super.onProgressUpdate(*values)
//            if (values.size >= 2) {
////                mCallback.onProgressUpdate(values[0], values[1])
//            }
//        }

//        override fun onPostExecute(result: List<DatabaseMessage>?) {
//
//            mCallback.updateFromDownload(result)
//
//            mCallback.finishDownloading()
//        }


//        override fun onCancelled(result: List<DatabaseMessage>?) {}

        @Throws(IOException::class)
        private fun downloadUrl(url: URL): List<DatabaseMessage>? {
            var stream: InputStream? = null
            var connection: HttpsURLConnection? = null
            val messages = ArrayList<DatabaseMessage>()
            var num = 0
            try {
                connection = url.openConnection() as HttpsURLConnection
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.readTimeout = 3000
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.connectTimeout = 3000
                // For this use case, set HTTP method to GET.
                connection.requestMethod = "GET"
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.doInput = true
                // Open communications link (network traffic occurs here).
                connection.connect()
//                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS)
                val responseCode = connection.responseCode
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }
                // Retrieve the response body as an InputStream.
                stream = connection.inputStream
//                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0)
                if (stream != null) {
                    val reader = JsonReader(InputStreamReader(stream, "UTF-8"))
                    reader.isLenient = true
                    try {
                        while (reader.hasNext() && num++ < massageNum) {
                            val message = readMessage(reader)
                            if (message != null) {
                                messages.add(message)
                            }
                            if (num % 100 == 0) {
                                insertAllMessages(*(messages.toTypedArray()))
                                messages.clear()
                            }
                            Log.d(TAG, "messages.size = ${messages.size}")
                            Log.d(TAG, "messages = $message")
                        }
                        insertAllMessages(*(messages.toTypedArray()))
                    } catch (e: IOException) {
                        Log.e(TAG, "Problem reading messages objects")
                    } finally {
                        reader.close()
                    }
//                    publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0)
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                stream?.close()
                connection?.disconnect()
            }
            return messages
        }

        @Throws(IOException::class)
        fun readMessage(reader: JsonReader): DatabaseMessage? {
            var toId = ""
            var toName = ""
            var fromId = ""
            var fromName = ""
            var timestamp = 0L
            var areFriends = false

            reader.beginObject()
            try {
                while (reader.hasNext()) {
                    val name = reader.nextName()
                    if (name == "to") {
                        val toUser = readUser(reader)
                        if (toUser == null) {
                            reader.skipValue()
                        } else {
                            toId = toUser.id
                            toName = toUser.name
                        }
                    } else if (name == "from") {
                        val fromUser = readUser(reader)
                        if (fromUser == null) {
                            reader.skipValue()
                        } else {
                            fromId = fromUser.id
                            fromName = fromUser.name
                        }
                    } else if (name == "timestamp") {
                        timestamp = reader.nextLong()
                    } else if (name == "areFriends") {
                        areFriends = reader.nextBoolean()
                    } else {
                        reader.skipValue()
                    }
                }
            } catch (e: IOException) {
                reader.endObject()
                return null
            } finally {
                reader.endObject()
                return DatabaseMessage(toId, toName, fromId, fromName, timestamp, areFriends)
            }
        }

        @Throws(IOException::class)
        fun readUser(reader: JsonReader): User? {
            var userName = ""
            var userId = ""

            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "name") {
                    userName = reader.nextString()
                } else if (name == "id") {
                    userId = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
            return User(userId, userName)
        }
    }
}


