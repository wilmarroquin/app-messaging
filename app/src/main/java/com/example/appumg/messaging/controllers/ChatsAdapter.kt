package com.example.appumg.messaging.controllers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.appumg.R
import com.example.appumg.messaging.models.Chat

class ChatsAdapter(private val onItemSelected: (Chat) -> Unit) :
    ListAdapter<Chat, ChatsViewHolder>(ChatDiffCallback()) {

    fun updateList(newChats: List<Chat>) {
        submitList(newChats.sortedByDescending { it.lastMessageTimestamp?.toDate() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_for_list_chat, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.render(getItem(position), onItemSelected)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean =
        oldItem.chatId == newItem.chatId

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean =
        oldItem == newItem
}
