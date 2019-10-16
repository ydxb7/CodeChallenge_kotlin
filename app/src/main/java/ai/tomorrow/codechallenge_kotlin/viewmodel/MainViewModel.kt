package ai.tomorrow.codechallenge_kotlin.viewmodel

import ai.tomorrow.codechallenge_kotlin.repository.MessageRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

const val CODE_CHALLENGE_URL = "https://codechallenge.secrethouse.party/"

class MainViewModel(
    private val application: Application
) : ViewModel(), KoinComponent {

    val messageRepository: MessageRepository by inject()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val messages = messageRepository.messages

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                messageRepository.reset(CODE_CHALLENGE_URL, 6000)
            }
        }


    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}