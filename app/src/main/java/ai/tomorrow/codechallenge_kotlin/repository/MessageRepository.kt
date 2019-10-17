package ai.tomorrow.codechallenge_kotlin.repository

import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MessageRepository(val messageDatasource: MessageDatasource) {

    private val mutex = Mutex()

    val messages = messageDatasource.messages

    val friendMessages = messageDatasource.friendMessages

    val noFriendMessages = messageDatasource.noFriendMessages

    fun getRelationFromPreference() = messageDatasource.getRelationFromPreference()

    fun saveRelationInPreference(valueResource: Int) =
        messageDatasource.saveRelationInPreference(valueResource)

    suspend fun reset(urlString: String, totoalNum: Int) {
        mutex.withLock {
            messageDatasource.clearAllMessages()
            messageDatasource.downloadToDatabase(urlString, totoalNum)
        }
    }

    suspend fun fetchNew(urlString: String, totoalNum: Int) {
        messageDatasource.downloadToDatabase(urlString, totoalNum)
    }

}