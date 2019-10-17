package ai.tomorrow.codechallenge_kotlin.message

import ai.tomorrow.codechallenge_kotlin.adapter.MessageRecyclerViewAdapter
import ai.tomorrow.codechallenge_kotlin.databinding.MessageFragmentBinding
import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
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

        adapter = MessageRecyclerViewAdapter(
            ArrayList(),
            object : MessageRecyclerViewAdapter.OnLoadMoreItemsListener {
                override fun onLoadMoreItems() {
                    viewModel.loadMoreMessages()
                }
            })
        binding.recyclerView.adapter = adapter


        binding.settingIv.setOnClickListener {
            val settingDialogFragment = SettingDialogFragment()
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

    private val observer = Observer<List<DatabaseMessage>> { adapter.insertNewData(it) }

    private fun showAllMessages() {
        viewModel.messagesFriend.removeObserver(observer)
        viewModel.messagesNoFriend.removeObserver(observer)
        if (viewModel.messagesAll.value != null) {
            adapter.setData(requireNotNull(viewModel.messagesAll.value))
        } else {
            adapter.setData(ArrayList())
        }
        viewModel.messagesAll.observe(this, observer)
    }

    private fun showFriendMessages() {
        viewModel.messagesAll.removeObserver(observer)
        viewModel.messagesNoFriend.removeObserver(observer)
        if (viewModel.messagesFriend.value != null) {
            adapter.setData(requireNotNull(viewModel.messagesFriend.value))
        } else {
            adapter.setData(ArrayList())
        }

        viewModel.messagesFriend.observe(this, observer)
    }

    private fun showNotFriendMessages() {
        viewModel.messagesAll.removeObserver(observer)
        viewModel.messagesFriend.removeObserver(observer)
        if (viewModel.messagesNoFriend.value != null) {
            adapter.setData(requireNotNull(viewModel.messagesNoFriend.value))
        } else {
            adapter.setData(ArrayList())
        }
        viewModel.messagesNoFriend.observe(this, observer)
    }
}