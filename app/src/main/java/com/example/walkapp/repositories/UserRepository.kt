package com.example.walkapp.repositories

import com.example.walkapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = Firebase.firestore

    suspend fun getUser(userId: String): User? {
        return try {
            val document = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists() && document.data != null) {
                User.mapToUser(userId, document.data!!)
            } else {
                User()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUserData(userId: String, userData: User) {
        try{
            db.collection("users")
                .document(userId)
                .update(userData.toMapUpdateUser())
                .await()
        }catch (e: Exception){
            throw e
        }
    }

    fun updateUserXp(batch: WriteBatch, userId: String, newDistanceTotal: Int){
        try{
            batch.update(
                db.collection("users").document(userId),
                "xp",
                FieldValue.increment(newDistanceTotal.toLong())
            )
        }catch (e: Exception){
            throw e
        }
    }
}
