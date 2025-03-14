package com.example.appumg.messaging.firebase

import android.util.Log
import com.example.appumg.messaging.models.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class MessagesCollection(chatId: String) {
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val messagesCollectionReference =
        firestoreInstance.collection("Chats").document(chatId).collection("Messages")

    fun insertMessage(messages: Messages) {
        messagesCollectionReference.add(messages)
            .addOnSuccessListener { documentReference -> Log.e("MessagesCollection.insertMessage",
                "Messages successfully inserted with ID ${documentReference.id}")
            }
            .addOnFailureListener { e -> Log.e("MessagesCollection.insertMessage", "Error inserting message: $e")
            }
    }

    fun updateMessage(message: Messages) {
        message.messageId?.let { id ->
            messagesCollectionReference.document(id)
                .set(message)
                .addOnSuccessListener {
                    Log.e("MessagesCollection.updateMessage", "Message successfully updated with ID $id")
                }
                .addOnFailureListener { e ->
                    Log.e("MessagesCollection.updateMessage", "Error updating message with ID $id: $e")
                }
        } ?: Log.e("MessagesCollection.updateMessage", "Error: messageId is null")
    }

    fun deleteMessage(message: Messages) {
        message.messageId?.let { id ->
            messagesCollectionReference.document(id)
                .delete()
                .addOnSuccessListener {
                    Log.e("MessagesCollection.deleteMessage", "Message successfully deleted with ID $id")
                }
                .addOnFailureListener { e ->
                    Log.e("MessagesCollection.deleteMessage", "Error deleting message with ID $id: $e")
                }
        } ?: Log.e("MessagesCollection.deleteMessage", "Error: messageId is null")
    }

    fun messagesToList(onSuccess: (MutableList<Messages>) -> Unit, onFailure: (Exception) -> Unit) {
        messagesCollectionReference.get()
            .addOnSuccessListener { messagesCollectionSnapshot ->
                val messageList = mutableListOf<Messages>()
                for (document in messagesCollectionSnapshot) {
                    val message = documentToMessageItem(document)
                    messageList.add(message)
                }
                onSuccess(messageList)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
    fun documentToMessageItem(document: QueryDocumentSnapshot?): Messages {
        val message = Messages()

        if (document != null) {  // Verificamos que no sea null antes de usarlo
            document.data.forEach { (key, value) ->
                when (key) {
                    "text" -> message.text = value.toString()
                    "senderId" -> message.senderId = value.toString()
                    //"messageTimestamp" -> message.messageTimestamp = document.getTimestamp("messageTimestamp") ?: Timestamp.now()
                    //"hasAttachedImage" -> message.hasAttachedImage = document.getBoolean("hasAttachedImage") ?: false
                }
            }
            message.messageId = document.id
        } else {
            Log.e("documentToMessageItem", "Error: el documento es null")
        }
        return message
    }

}