package com.example.walkapp.repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class BadgeRepository {
    private val db = Firebase.firestore

    suspend fun getBadges(userId: String): Map<String, Any> {
        try{
            val badgeRef = db.collection("users")
                .document(userId).collection("badgeData")
                .document("badge")

            val snapshot = badgeRef.get().await()
            return if(snapshot.exists()){
                snapshot.data ?: emptyMap()
            }else{
                emptyMap()
            }
        }catch (e: Exception){
            Log.e("UserRepository", "Error getting badges", e)
            throw e
        }
    }
}