package com.example.appumg.messaging.firebase

import android.R
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.appumg.messaging.models.User
import kotlinx.coroutines.tasks.await

class UsersCollection {
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollectionReference = firestoreInstance.collection("Users")
    private val userList: Query = userCollectionReference.orderBy("userName", Query.Direction.DESCENDING)

    fun insertUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        userCollectionReference.add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("UserCollection", "User inserted with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UserCollection", "Error inserting user: ", e)
                onFailure(e)
            }
    }

    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = user.userId ?: return
        userCollectionReference.document(userId).set(user)
            .addOnSuccessListener {
                Log.d("UserCollection", "User updated with ID: $userId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UserCollection", "Error inserting user: ", e)
                onFailure(e)
            }
    }

    fun usersToList(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit){
        userList.get()
            .addOnSuccessListener { querySnapshot ->
                val userList = querySnapshot.documents.mapNotNull { it.toObject(User::class.java) }
                Log.d("UsersCollection", "Fetched ${userList.size} users")
                onSuccess(userList)
            }
            .addOnFailureListener { e ->
                Log.e("userCollection", "Error Fetched users: ", e)
                onFailure(e)
            }
    }
    suspend fun getUser(userId: String): User?{
        return try {
            val querySnapshot = userCollectionReference.whereEqualTo("userId", userId).get().await()
            querySnapshot.documents.firstOrNull()?.toObject(User::class.java)
        } catch(e: Exception) {
            Log.e("userCollection", "Error Fetched user:", e)
            null
        }
    }
}