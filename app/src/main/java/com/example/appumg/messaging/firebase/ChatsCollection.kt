package com.example.appumg.messaging.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.example.appumg.messaging.models.Chat
import com.google.firebase.firestore.model.Document
import kotlinx.coroutines.tasks.await
import java.util.ArrayList
import kotlin.math.log

class ChatsCollection(userId: String) {
    private val firestoreInstance = FirebaseFirestore.getInstance()
    val chatCollectionReference = firestoreInstance.collection("Chats")
    val userChatList: Query = chatCollectionReference.whereArrayContains("membersId", userId)
        .orderBy("creationTimestamp", Query.Direction.DESCENDING)

    suspend fun insertChat(chat: Chat): String? {
        return try {
            chat.creationTimestamp = Timestamp.now()
            val documentReference = chatCollectionReference.add(chat).await()
            Log.e(
                "ChatsCollection.insertChat",
                "Chat successfully inserted with ID ${documentReference.id}"
            )
            documentReference.id
        } catch (e: Exception) {
            Log.e("ChatsCollection.insertChat", "Error inserting chat: $e")
            null
        }
    }

    suspend fun updateChat(chat: Chat) {
        val chatId = chat.chatId.orEmpty()
        chat.chatId = null
        try {
            chatCollectionReference.document(chatId).set(chat).await()
            Log.e("ChatsCollection.updateChat", "Chat successfully updated with ID $chatId")
        } catch (e: Exception) {
            Log.e("ChatsCollection.updateChat", "Error updating the chat with ID $chatId: $e")
        }
    }

    suspend fun deleteChat(chat: Chat) {
        try {
            chatCollectionReference.document(chat.chatId.orEmpty()).delete().await()
            Log.e("ChatsCollection.deleteChat", "Chat successfully deleted with ID ${chat.chatId}")
        } catch (e: Exception) {
            Log.e(
                "ChatsCollection.deleteChat",
                "Error deleting the chat with ID ${chat.chatId}: $e"
            )
        }
    }

    suspend fun chatsToList(): List<Chat> {
        return try {
            val snapshot = userChatList.get().await()
            snapshot.documents.map { documentToChatItem(it) }
        } catch (e: Exception) {
            Log.e("ChatsCollection.chatsToList", "Error fetching chat list: $e")
            emptyList()
        }
    }

    suspend fun getChat(chatId: String): Chat? {
        return try {
            val documentSnapshot = chatCollectionReference.document(chatId).get().await()
            documentToChatItem(documentSnapshot)
        } catch (e: Exception) {
            Log.e("ChatCollection.getChat", "Error fetching chat with ID $chatId: $e")
            null
        }
    }
}

    private fun documentToChatItem(document: DocumentSnapshot): Chat{
        return Chat().apply {
            chatId = document.id
            chatName = document.getString("chatName").orEmpty()
            creatorId = document.getString("creatorId").orEmpty()
            iconUrl = document.getString("iconUrlId").orEmpty()
            hasCustomIcon = document.getBoolean("hasCustomIcon")?: false
            creationTimestamp = document.getTimestamp("creationTimestamp")?: Timestamp.now()
            lastMessageTimestamp = document.getTimestamp("lastMessageTimestamp")?: Timestamp.now()
            lastMessage = document.getString("lastMessage").orEmpty()
            membersId = (document.get("membersId") as? List<*>)?.mapNotNull {
                it as? String }?.toCollection(ArrayList()) ?: arrayListOf()
            administratorsId = (document.get("administratorsId") as? List<*>)?.mapNotNull {
                it as? String }?.toCollection(ArrayList()) ?: arrayListOf()
        }

    }