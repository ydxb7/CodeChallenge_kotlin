package ai.tomorrow.codechallenge_kotlin.message

import ai.tomorrow.codechallenge_kotlin.adapter.MessageRecyclerViewAdapter
import ai.tomorrow.codechallenge_kotlin.databinding.MessageFragmentBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel

class MessageFragment : Fragment() {

    private val TAG = "MessageFragment"

    private lateinit var binding: MessageFragmentBinding
    private val viewModel: MessageViewModel by viewModel()
    private lateinit var adapter: MessageRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MessageFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        adapter = MessageRecyclerViewAdapter(ArrayList())
        binding.recyclerView.adapter = adapter


        binding.settingIv.setOnClickListener {
            val settingDialogFragment =
                SettingDialogFragment()
            settingDialogFragment.show(
                requireNotNull(fragmentManager),
                SettingDialogFragment::class.simpleName
            )
        }

        viewModel.messageType.observe(this, Observer {
            when (it) {
                MessageType.ALL -> showAllMessages()
                MessageType.FRIEND -> showFriendMessages()
                MessageType.NOTFRIEND -> showNotFriendMessages()
            }
        })


        return binding.root
    }

    private fun showAllMessages() {
        if (viewModel.messagesAll.value != null) {
            adapter.setData(requireNotNull(viewModel.messagesAll.value))
        } else {
            adapter.setData(ArrayList())
        }
        viewModel.messagesAll.observe(this, Observer {
            adapter.insertNewData(it)
        })
    }

    private fun showFriendMessages() {
        if (viewModel.messagesFriend.value != null) {
            adapter.setData(requireNotNull(viewModel.messagesFriend.value))
        } else {
            adapter.setData(ArrayList())
        }

        viewModel.messagesFriend.observe(this, Observer {
            adapter.insertNewData(it)
        })
    }

    private fun showNotFriendMessages() {
        if (viewModel.messagesNoFriend.value != null) {
            adapter.setData(requireNotNull(viewModel.messagesNoFriend.value))
        } else {
            adapter.setData(ArrayList())
        }
        viewModel.messagesNoFriend.observe(this, Observer {
            adapter.insertNewData(it)
        })
    }
}