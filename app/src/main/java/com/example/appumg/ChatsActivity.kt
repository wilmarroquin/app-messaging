package com.example.appumg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.content.Intent
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appumg.databinding.ActivityChatsBinding
import com.example.appumg.messaging.models.Chat
import com.example.appumg.messaging.controllers.ChatsAdapter
import com.example.appumg.messaging.firebase.ChatsCollection
import com.google.firebase.firestore.DocumentChange

class ChatsActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var binding: ActivityChatsBinding
    private lateinit var userChatsCollection: ChatsCollection
    private lateinit var chatsAdapter: ChatsAdapter
    private val chatsList = mutableListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initUI()
        initListeners()
    }

    private fun initComponents(){
        userId = intent.getStringExtra("userId").orEmpty()
        if(userId.isEmpty()){
            Toast.makeText(this, "Error: Usuario no valido", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Log.e("ChatsActivity", "Usuario ID: $userId")

        userChatsCollection = ChatsCollection(userId)
        subscribeToChatUpdates()
    }
    private fun initUI() {
        chatsAdapter = ChatsAdapter { chat -> navigateToChatRoom(chat) }
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter = chatsAdapter
        chatsAdapter.submitList(chatsList) // Asignar lista de chats al adaptador
    }
    private fun navigateToChatRoom(chat: Chat){
        Intent(this, ChatRoomActivity::class.java).apply{
            putExtra("userId", userId)
            putExtra("chatId", chat.chatId)
            Log.e("navigateToChatRoom", "$userId - ${chat.chatId}")
            startActivity(this)
        }
    }
    private fun subscribeToChatUpdates(){
        userChatsCollection.userChatList.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, "Error en Firestore: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.documentChanges?.forEach { dc ->
                when(dc.type){
                    DocumentChange.Type.ADDED ->{
                        val newChat = userChatsCollection.documentToChatItem(dc.document)
                        chatsList.add(newChat)
                        chatsAdapter.notifyItemInserted(chatsList.size - 1)
                    }
                    DocumentChange.Type.REMOVED ->{
                        val index = chatsList.indexOfFirst { it.chatId == dc.document.id }
                        if (index != -1){
                            chatsList[index] = userChatsCollection.documentToChatItem(dc.document)
                            chatsAdapter.notifyItemRemoved(index)
                        }
                    }
                    DocumentChange.Type.MODIFIED ->{
                        val index = chatsList.indexOfFirst { it.chatId == dc.document.id }
                        if(index != -1){
                            chatsList[index] = userChatsCollection.documentToChatItem(dc.document)
                            chatsAdapter.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }
    private fun initListeners(){
        binding.cvAddChat.setOnClickListener {
            Intent(this, NewChatActivity::class.java).apply {
                putExtra("userId", userId)
                startActivity(this)
            }
        }
    }
}