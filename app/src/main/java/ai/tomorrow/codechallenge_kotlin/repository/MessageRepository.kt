package ai.tomorrow.codechallenge_kotlin.repository

import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MessageRepository(val messageDatasource: MessageDatasource) {

    private val mutex = Mutex()

    val messages = messageDatasource.messages

    suspend fun reset(urlString: String, totoalNum: Int) {
        mutex.withLock {
            messageDatasource.clearAllMessages()
            val newMessages = messageDatasource.downloadUrl(urlString, totoalNum)
            if (!newMessages.isNullOrEmpty()) {
                messageDatasource.insertAllMessages(*newMessages.toTypedArray())
            }
        }
    }


}