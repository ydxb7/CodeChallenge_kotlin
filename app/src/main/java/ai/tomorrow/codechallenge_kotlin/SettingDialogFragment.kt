package ai.tomorrow.codechallenge_kotlin

import ai.tomorrow.codechallenge_kotlin.databinding.SettingDialogFragmentBinding
import ai.tomorrow.codechallenge_kotlin.repository.MessageRepository
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject

class SettingDialogFragment : DialogFragment() {

    private val TAG = "SettingDialogFragment"
    private lateinit var binding: SettingDialogFragmentBinding
    private val messageRepository: MessageRepository by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingDialogFragmentBinding.inflate(inflater, container, false)

        setupSharedPreferences()

        savePreference()

        return binding.root
    }

    private fun savePreference() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.friendRadio) {
                messageRepository.saveRelationInPreference(R.string.pref_friends_value)
            } else if (checkedId == R.id.noFriendRadio) {
                messageRepository.saveRelationInPreference(R.string.pref_no_friends_value)
            } else {
                messageRepository.saveRelationInPreference(R.string.pref_all_value)
            }
        }
    }

    private fun setupSharedPreferences() {
        messageRepository.getRelationFromPreference().also {
            Log.d(TAG, "setupSharedPreferences: $it")
            when (it) {
                requireContext().resources.getString(R.string.pref_all_value) ->
                    binding.radioGroup.check(R.id.allRadio)
                requireContext().resources.getString(R.string.pref_friends_value) ->
                    binding.radioGroup.check(R.id.friendRadio)
                requireContext().resources.getString(R.string.pref_no_friends_value) ->
                    binding.radioGroup.check(R.id.noFriendRadio)
            }
        }
    }
}