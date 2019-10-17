package ai.tomorrow.codechallenge_kotlin.adapter

import ai.tomorrow.codechallenge_kotlin.databinding.ListItemBinding
import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MessageRecyclerViewAdapter(private var mData: List<DatabaseMessage>) :
    RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mData.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = mData[position]
        holder.bind(position, message)
    }

    fun setData(newData: List<DatabaseMessage>) {
        mData = newData
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, message: DatabaseMessage) {
            binding.listIndexTv.text = position.toString()
            binding.message = message
            val timeString =
                SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(message.timestamp.toLong())
            binding.timeTv.text = timeString
            if (message.areFriends) {
                binding.friendTv.text = "friends"
            } else {
                binding.friendTv.text = "not friends"
            }
        }
    }
}