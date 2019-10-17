package ai.tomorrow.codechallenge_kotlin.datasource

import ai.tomorrow.codechallenge_kotlin.R
import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
import ai.tomorrow.codechallenge_kotlin.model.User
import ai.tomorrow.codechallenge_kotlin.model.getDatabase
import ai.tomorrow.codechallenge_kotlin.utils.NoNewLineInputStreamReader
import android.app.Application
import android.util.JsonReader
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MessageDatasource(val application: Application) {

    private val TAG = "MessageDatasource"
    private val mutex = Mutex()

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    val database = getDatabase(application).messageDao

    val messages = database.getAllMessages()

    val friendMessages = database.getFriends()

    val noFriendMessages = database.getNoFriends()

    fun clearAllMessages() = database.clear()

    fun insertAllMessages(vararg m: DatabaseMessage) = database.insertAll(*m)

    fun getRelationFromPreference(): String {
        return sharedPreferences.getString(
            application.getString(R.string.pref_friend_key),
            application.getString(R.string.pref_all_value)
        ) ?: application.getString(R.string.pref_all_value)
    }

    fun saveRelationInPreference(valueResource: Int) {
        sharedPreferences.edit {
            putString(
                application.getString(R.string.pref_friend_key),
                application.getString(valueResource)
            )
        }
    }


    suspend fun downloadToDatabase(urlString: String, messageNum: Int) {
        mutex.withLock {
            Log.d(TAG, "downloadUrl")
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

                    val noNewLineInputStreamReader = NoNewLineInputStreamReader(stream, "UTF-8")
                    val reader = JsonReader(noNewLineInputStreamReader)
                    reader.isLenient = true

                    while (reader.hasNext() && ++num < messageNum) {
                        val message = readMessage(reader)
                        if (message != null) {
                            messages.add(message)
                            if (num % 100 == 0) {
                                insertAllMessages(*messages.toTypedArray())
                                messages.clear()
                            }
                        }
                        Log.d(TAG, "messages.size = ${messages.size}")
                    }
                    insertAllMessages(*messages.toTypedArray())
                    reader.close()
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                stream?.close()
                connection?.disconnect()
            }
        }

    }

    private fun readMessage(reader: JsonReader): DatabaseMessage? {
        var toId = ""
        var toName = ""
        var fromId = ""
        var fromName = ""
        var timestamp = ""
        var areFriends = false

        reader.beginObject()
        try {
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "to") {
                    val toUser = readUser(reader)
                    if (toUser == null) {
                        endObject(reader)
                        return null
                    } else {
                        toId = toUser.id
                        toName = toUser.name
                    }
                } else if (name == "from") {
                    val fromUser = readUser(reader)
                    if (fromUser == null) {
                        endObject(reader)
                        return null
                    } else {
                        fromId = fromUser.id
                        fromName = fromUser.name
                    }
                } else if (name == "timestamp") {
                    timestamp = reader.nextString()
                } else if (name == "areFriends") {
                    areFriends = reader.nextBoolean()
                } else {
                    reader.skipValue()
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "readMessage IllegalStateException: $e")
            endObject(reader)
            return null
        }
        endObject(reader)
        return DatabaseMessage(toId, toName, fromId, fromName, timestamp, areFriends)
    }

    private fun endObject(reader: JsonReader) {
        try {
            while (reader.hasNext()) {
                reader.skipValue()
            }
            reader.endObject()
        } catch (e: Exception) {
            Log.e(TAG, "endObject Exception: $e")
            return
        }
    }

    fun readUser(reader: JsonReader): User? {
        var userName = ""
        var userId = ""

        reader.beginObject()
        try {
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
        } catch (e: IllegalStateException) {
            Log.d(TAG, "readUser IllegalStateException: $e")
            endObject(reader)
            return null
        }
        endObject(reader)
        return User(userId, userName)
    }
}


