package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.User
import com.example.walkapp.views.historicscreen.WalkHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
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

    suspend fun saveWalkingData(userId: String, totalDistance: Double, elapsedTime: Long) {
        try {
            val walkingData = mapOf(
                "totalDistance" to totalDistance,
                "elapsedTime" to elapsedTime,
                "timestamp" to FieldValue.serverTimestamp()
            )

            db.collection("users")
                .document(userId)
                .collection("walkingData")
                .add(walkingData)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getWalkHistory(userId: String, limit: Long, lastDocument: DocumentSnapshot? = null): Pair<List<WalkHistoryItem>, DocumentSnapshot?> {
        try {
            var query = db.collection("users")
                .document(userId)
                .collection("walkingData")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)

            if (lastDocument != null) {
                query = query.startAfter(lastDocument)
            }

            val querySnapshot = query.get().await()

            if (querySnapshot.isEmpty) {
                return Pair(emptyList(), null)
            }

            val walkHistoryItems = querySnapshot.documents.mapNotNull { document ->
                val totalDistance = document.getDouble("totalDistance") ?: 0.0
                val elapsedTime = document.getLong("elapsedTime") ?: 0L
                val timestamp = document.getTimestamp("timestamp")?.toDate()?.toString() ?: ""

                WalkHistoryItem(
                    timestamp = timestamp,
                    distance = totalDistance.toString(),
                    elapsedTimeMs = elapsedTime
                )
            }

            val lastVisibleDocument = querySnapshot.documents.lastOrNull()

            return Pair(walkHistoryItems, lastVisibleDocument)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching walk history", e)
            throw e
        }
    }
}