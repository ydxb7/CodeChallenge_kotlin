package ai.tomorrow.codechallenge_kotlin.message

import ai.tomorrow.codechallenge_kotlin.adapter.MessageRecyclerViewAdapter
import ai.tomorrow.codechallenge_kotlin.databinding.MessageFragmentBinding
import ai.tomorrow.codechallenge_kotlin.viewmodel.MainViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import org.koin.android.viewmodel.ext.android.viewModel

class MessageFragment : Fragment() {

    private val TAG = "MessageFragment"

    private lateinit var binding: MessageFragmentBinding
    private val viewModel: MainViewModel by viewModel()
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
            val direction = MessageFragmentDirections.actionMessageFragmentToSettingDialogFragment()
            it.findNavController().navigate(direction)
        }


        viewModel.messages.observe(this, Observer {
            Log.d(TAG, "message.size = ${it.size}")
//            Log.d(TAG, "binding.textView = ${binding.mainTv}")

            adapter.setData(it)
//            binding.mainTv.text = it.toString()
        })

        return binding.root
    }
}