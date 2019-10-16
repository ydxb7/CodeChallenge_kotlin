package ai.tomorrow.codechallenge_kotlin.datasource

import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
import ai.tomorrow.codechallenge_kotlin.model.User
import ai.tomorrow.codechallenge_kotlin.model.getDatabase
import android.app.Application
import android.util.JsonReader
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


//const val MASSAGE_NUM = 200
//const val CODE_CHALLENGE_URL = "https://www.google.com"

class MessageDatasource(application: Application) {

    private val TAG = "MessageDatasource"
    private val mutex = Mutex()

    val database = getDatabase(application).messageDao

    val messages = database.getAllMessages()

    fun clearAllMessages() = database.clear()

    fun insertAllMessages(vararg m: DatabaseMessage) = database.insertAll(*m)
    

    @Throws(IOException::class)
    fun downloadUrl(urlString: String, messageNum: Int): List<DatabaseMessage>? {
        val url = URL(urlString)
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
            val responseCode = connection.responseCode
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw IOException("HTTP error code: $responseCode")
            }
            // Retrieve the response body as an InputStream.
            stream = connection.inputStream
            if (stream != null) {
                val reader = JsonReader(InputStreamReader(stream, "UTF-8"))
                reader.isLenient = true
                try {
                    while (reader.hasNext() && num++ < messageNum) {
                        val message = readMessage(reader)
                        if (message != null) {
                            messages.add(message)
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
        } catch (e: IllegalStateException) {
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


