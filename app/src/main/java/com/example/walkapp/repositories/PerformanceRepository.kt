package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.Performance
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class PerformanceRepository {
    private val db = Firebase.firestore

    suspend fun getPerformanceData(userId: String): Performance? {
        try {
            val document = db.collection("users")
                .document(userId)
                .collection("performanceData")
                .document("performance")
                .get()
                .await()

            if (!document.exists() || document.data == null) {
                return null
            }
            Log.d("PerformanceRepository", "Document data: ${document.data}")
            return Performance.mapToPerformance(document.data!!)
        } catch (e: Exception) {
            Log.e("PerformanceRepository", "Error getting performance data", e)
            throw e
        }
    }
}