package com.example.walkapp.repositories

import com.example.walkapp.models.User
import com.google.firebase.Firebase
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

    suspend fun saveUserData(userData: User) {
        try {
            db.collection("users")
                .document(userData.id)
                .set(userData.toMap())
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