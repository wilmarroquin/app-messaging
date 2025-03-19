package com.example.appumg.messaging.controllers

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appumg.databinding.ItemForListChatBinding
import com.example.appumg.messaging.models.Chat
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class ChatsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemForListChatBinding.bind(view)

    fun render(chat: Chat, onItemSelected: (Chat) -> Unit){
        binding.tvChatName.text = chat.chatName
        binding.tvChatTimeStamp.text = dateFormatter(chat.lastMessageTimestamp.toDate())
        binding.tvChatLastMessage.text = chat.lastMessage

        //val iconUrl = chat.iconUrl
        if (chat.hasCustomIcon) {
            val iconUrl = chat.iconUrl
            if (!iconUrl.isNullOrEmpty()) {
                Glide.with(binding.ivChatIcon.context)
                    .load(iconUrl)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(binding.ivChatIcon)
            } else {
                binding.ivChatIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        } else {
            binding.ivChatIcon.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        binding.root.setOnClickListener { onItemSelected(chat) }
    }

    private fun dateFormatter(date: Date): String{
        val calendarReference = Calendar.getInstance()
        val thisDate = Calendar.getInstance().apply { time = date }

        return when{
            thisDate.get(Calendar.DAY_OF_YEAR) == calendarReference.get(Calendar.DAY_OF_YEAR) &&
                    thisDate.get(Calendar.YEAR) == calendarReference.get(Calendar.YEAR) ->{
                        Log.e("ChatsViewHolder.dateFormatter", "Mismo dia")
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                    }
            thisDate.get(Calendar.WEEK_OF_YEAR) == calendarReference.get(Calendar.WEEK_OF_YEAR) &&
                    thisDate.get(Calendar.YEAR) == calendarReference.get(Calendar.YEAR) ->{
                        Log.e("ChatViewHolder.dateFormatter", "Misma semana")
                SimpleDateFormat("EEEE HH:mm", Locale.getDefault()).format(date)
                    }
            thisDate.get(Calendar.YEAR) == calendarReference.get(Calendar.YEAR) ->{
                Log.e("ChatsViewHolder.dateFormatter", "Mismo año")
                SimpleDateFormat("d MMMM", Locale.getDefault()).format(date)
            }
            else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
        }
    }
}