package com.example.appumg.messaging.controllers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.appumg.databinding.ItemForNewChatBinding
import com.example.appumg.messaging.models.User

//import com.google.firebase.firestore.core.View

class NewChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemForNewChatBinding.bind(view)

    fun render(user: User, onItemSelected: (View) -> Unit) {
        binding.tvUserEmailNewChat.text = user.userEmail
        binding.tvUserNameNewChat.text = user.userName

        if (user.hasCustomIcon) {
            TODO("Agregar código para obtener imagen de Cloud Storage")
        } else {
            binding.ivUserProfilePic.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        binding.root.setOnClickListener { onItemSelected(binding.root) }
    }
}
