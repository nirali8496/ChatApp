package com.example.chatapp.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.data.model.ChatMessage

class ChatMessageAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentText: TextView = itemView.findViewById(R.id.tv_sent)
        val receivedText: TextView = itemView.findViewById(R.id.tv_received)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        if (message.isSent) {
            holder.sentText.text = message.message
            holder.sentText.visibility = View.VISIBLE
            holder.receivedText.visibility = View.GONE
        } else {
            holder.receivedText.text = message.message
            holder.receivedText.visibility = View.VISIBLE
            holder.sentText.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
}
