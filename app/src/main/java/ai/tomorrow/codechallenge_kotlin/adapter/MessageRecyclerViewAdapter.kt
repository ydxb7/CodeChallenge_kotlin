package ai.tomorrow.codechallenge_kotlin.adapter

import ai.tomorrow.codechallenge_kotlin.databinding.ListItemBinding
import ai.tomorrow.codechallenge_kotlin.model.DatabaseMessage
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MessageRecyclerViewAdapter(
    private var mData: List<DatabaseMessage>,
    private val mOnLoadMoreItemsListener: OnLoadMoreItemsListener
) :
    RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {

    interface OnLoadMoreItemsListener {
        fun onLoadMoreItems()
    }

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
        if (position == itemCount - 10) {
            mOnLoadMoreItemsListener.onLoadMoreItems()
        }
    }

    fun setData(newData: List<DatabaseMessage>) {
        mData = newData
        notifyDataSetChanged()
    }

    fun insertNewData(newData: List<DatabaseMessage>) {
        val oldSize = mData.size
        val newSize = newData.size
        mData = newData
        if (oldSize < newSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else {
            notifyDataSetChanged()
        }
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
                binding.friendTv.setTextColor(Color.GREEN)
                binding.colorBar.setBackgroundColor(Color.GREEN)
            } else {
                binding.friendTv.text = "not friends"
                binding.friendTv.setTextColor(Color.RED)
                binding.colorBar.setBackgroundColor(Color.RED)
            }
        }
    }
}