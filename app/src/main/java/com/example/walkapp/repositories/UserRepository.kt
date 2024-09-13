package com.example.walkapp.repositories

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = Firebase.firestore

    suspend fun getUser(userId: String): Map<String, Any>? {
        return try {
            val document = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                document.data ?: emptyMap()
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserData(userId: String, userData: Map<String, Any>) {
        try {
            db.collection("users")
                .document(userId)
                .set(userData)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun isNicknameUnique(nickname: String): Boolean {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("nickname", nickname)
                .get()
                .await()

            querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateAvatarIndex(userId: String, avatarIndex: Int) {
        try {
            db.collection("users")
                .document(userId)
                .update("avatarIndex", avatarIndex)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}