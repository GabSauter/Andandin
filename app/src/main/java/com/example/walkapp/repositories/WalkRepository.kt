package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.views.historicscreen.WalkHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WalkRepository(private val perfomanceRepository: PerformanceRepository, private val badgeRepository: BadgeRepository) {
    private val db = Firebase.firestore

    suspend fun completeWalk(userId: String, distance: Double, elapsedTime: Long) {
        try {
            val calendar = Calendar.getInstance()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val todayString = dateFormat.format(calendar.time)
            val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            val monthFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val currentMonth = monthFormat.format(calendar.time)

            val userRef = db.collection("users").document(userId)
            val walkingDataRef = userRef.collection("walkingData").document()
            val performanceDataRef = userRef.collection("performanceData").document("performance")
            val badgesRef = userRef.collection("badgeData").document("badge")

            db.runTransaction { transaction ->
                val performance = perfomanceRepository.getPerformanceData(transaction, performanceDataRef)
                val badgeData = badgeRepository.getBadgeData(transaction, badgesRef)

                val newDistanceTotal = performance.distanceTotal + distance
                perfomanceRepository.setPerformanceData(transaction, performanceDataRef, performance, distance, newDistanceTotal, todayString, currentMonth)
                setWalkingData(transaction, walkingDataRef, distance, elapsedTime, todayString)
                badgeRepository.setBadges(transaction, badgesRef, distance, newDistanceTotal, badgeData)
                null
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun setWalkingData(transaction: Transaction, walkingDataRef: DocumentReference, distance: Double, elapsedTime: Long, todayString: String){
        val walkingData = mapOf(
            "distance" to distance,
            "time" to elapsedTime,
            "date" to todayString
        )
        transaction.set(walkingDataRef, walkingData)
    }

    suspend fun getWalkHistory(userId: String, limit: Long, lastDocument: DocumentSnapshot? = null): Pair<List<WalkHistoryItem>, DocumentSnapshot?> {
        try {
            var query = db.collection("users")
                .document(userId)
                .collection("walkingData")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit)

            if (lastDocument != null) {
                query = query.startAfter(lastDocument)
            }

            val querySnapshot = query.get().await()
            if (querySnapshot.isEmpty) {
                return Pair(emptyList(), null)
            }

            val walkHistoryItems = querySnapshot.documents.mapNotNull { document ->
                val distance = document.getDouble("distance") ?: 0.0
                val elapsedTime = document.getLong("time") ?: 0L
                val date = document.getString("date").toString()

                WalkHistoryItem(
                    date = date,
                    distance = distance,
                    time = elapsedTime
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