package com.example.appumg.messaging.controllers

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appumg.databinding.ItemForListChatRoomBinding
import com.example.appumg.messaging.models.Messages
import com.example.appumg.messaging.models.User
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemForListChatRoomBinding.bind(view)

    fun render(messages: Messages, user: User, onItemSelected: (Messages) -> Unit) {
        binding.tvUserName.text = user.userName
        binding.tvMessage.text = messages.text
        binding.tvMessageTimeStamp.text = dateFormatter(messages.messageTimestamp.toDate())

        if (messages.hasAttachedImage) {
            Glide.with(binding.root.context)
                .load("URL_DE_LA_IMAGEN")
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(binding.ivUserIcon)
        } else {
            binding.ivUserIcon.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        binding.root.setOnClickListener { onItemSelected(messages) }
    }

    private fun dateFormatter(date: Date): String {
        val calendarNow = Calendar.getInstance()
        val calendarMessage = Calendar.getInstance().apply { time = date }

        val formatSameDay = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formatSameWeek = SimpleDateFormat("EEEE HH:mm", Locale.getDefault())
        val formatSameYear = SimpleDateFormat("d MMMM", Locale.getDefault())
        val formatDMY = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        return when {
            calendarMessage.get(Calendar.DAY_OF_YEAR) == calendarNow.get(Calendar.DAY_OF_YEAR) &&
                    calendarMessage.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) -> {
                Log.e("MessageViewHolder", "Mismo día")
                formatSameDay.format(date)
            }
            calendarMessage.get(Calendar.WEEK_OF_YEAR) == calendarNow.get(Calendar.WEEK_OF_YEAR) &&
                    calendarMessage.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) -> {
                Log.e("MessageViewHolder", "Misma semana")
                formatSameWeek.format(date)
            }
            calendarMessage.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR) -> {
                Log.e("MessageViewHolder", "Mismo año")
                formatSameYear.format(date)
            }
            else -> formatDMY.format(date)
        }
    }
}
