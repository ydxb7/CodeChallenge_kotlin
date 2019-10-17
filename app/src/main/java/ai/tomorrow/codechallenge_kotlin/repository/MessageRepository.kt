package ai.tomorrow.codechallenge_kotlin.repository

import ai.tomorrow.codechallenge_kotlin.datasource.MessageDatasource
import kotlinx.coroutines.sync.Mutex

class MessageRepository(val messageDatasource: MessageDatasource) {

    private val mutex = Mutex()

    val messages = messageDatasource.messages

    suspend fun reset(urlString: String, totoalNum: Int) {
        messageDatasource.clearAllMessages()
        val newMessages = messageDatasource.downloadToDatabase(urlString, totoalNum)

    }


}