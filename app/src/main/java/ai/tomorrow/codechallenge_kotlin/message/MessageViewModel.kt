package ai.tomorrow.codechallenge_kotlin.message

import ai.tomorrow.codechallenge_kotlin.R
import ai.tomorrow.codechallenge_kotlin.repository.MessageRepository
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

const val CODE_CHALLENGE_URL = "https://codechallenge.secrethouse.party/"
const val MESSAGE_TOTAL_NUM = 2000

enum class MessageType {
    ALL, FRIEND, NOTFRIEND
}

class MessageViewModel(
    private val application: Application
) : ViewModel(), KoinComponent, SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "MessageViewModel"

    val messageRepository: MessageRepository by inject()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val messagesAll = messageRepository.messages
    val messagesFriend = messageRepository.friendMessages
    val messagesNoFriend = messageRepository.noFriendMessages

    private val _messageType = MutableLiveData<MessageType>()
    val messageType: LiveData<MessageType>
        get() = _messageType


    init {
        PreferenceManager.getDefaultSharedPreferences(application)
            .registerOnSharedPreferenceChangeListener(this)
        resetMessages()

        setMessageType()
    }

    private fun setMessageType() {
        messageRepository.getRelationFromPreference().also {
            when (it) {
                application.getString(R.string.pref_all_value) ->
                    _messageType.value = MessageType.ALL
                application.getString(R.string.pref_friends_value) ->
                    _messageType.value = MessageType.FRIEND
                application.getString(R.string.pref_no_friends_value) ->
                    _messageType.value =
                        MessageType.NOTFRIEND
            }
        }
    }

    fun resetMessages() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                messageRepository.reset(CODE_CHALLENGE_URL, MESSAGE_TOTAL_NUM)
            }
        }
    }

    fun loadMoreMessages() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                messageRepository.fetchMore(CODE_CHALLENGE_URL, MESSAGE_TOTAL_NUM)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "Preference value = ${messageRepository.getRelationFromPreference()}")
        if (key == application.getString(R.string.pref_friend_key)) {
            when (messageRepository.getRelationFromPreference()) {
                application.getString(R.string.pref_all_value) ->
                    _messageType.value = MessageType.ALL

                application.getString(R.string.pref_friends_value) ->
                    _messageType.value = MessageType.FRIEND

                application.getString(R.string.pref_no_friends_value) ->
                    _messageType.value =
                        MessageType.NOTFRIEND
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        PreferenceManager.getDefaultSharedPreferences(application)
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}