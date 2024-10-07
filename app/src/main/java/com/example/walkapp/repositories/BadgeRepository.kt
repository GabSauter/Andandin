package com.example.walkapp.repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class BadgeRepository {
    private val db = Firebase.firestore

    suspend fun getBadges(userId: String): List<Int> {
        try{
            val badgeRef = db.collection("users")
                .document(userId).collection("badgeData")
                .document("badge")

            val snapshot = badgeRef.get().await()
            return if(snapshot.exists()){
                snapshot.get("badges") as List<Int>
            }else{
                mutableListOf(0,0,0,0,0,0,0,0,0)
            }
        }catch (e: Exception){
            Log.e("UserRepository", "Error getting badges", e)
            throw e
        }
    }
}