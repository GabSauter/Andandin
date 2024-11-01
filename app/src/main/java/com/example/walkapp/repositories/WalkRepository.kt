package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.views.historicscreen.WalkHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WalkRepository(
    private val userRepository: UserRepository,
    private val performanceRepository: PerformanceRepository,
    private val badgeRepository: BadgeRepository
) {
    private val db = Firebase.firestore

    suspend fun completeWalk(userId: String, distance: Int, elapsedTime: Long) {
        try {
            val calendar = Calendar.getInstance()
            val dateAndTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val todayWithTime = dateAndTimeFormat.format(calendar.time)
            val today = todayWithTime.split(" ")[0]
            val monthFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val currentMonth = monthFormat.format(calendar.time)

            val userRef = db.collection("users").document(userId)
            val walkingDataRef = userRef.collection("walkingData").document()
            val performanceDataRef = userRef.collection("performanceData").document("performance")
            val badgesRef = userRef.collection("badgeData").document("badge")

            val performance = performanceRepository.getPerformanceData(userId)
            val badgeData = badgeRepository.getBadges(userId)

            val newDistanceTotal = performance.distanceTotal + distance

            val batch = db.batch()

            performanceRepository.setPerformanceDataBatch(
                batch,
                performanceDataRef,
                performance,
                distance,
                newDistanceTotal,
                today,
                currentMonth
            )
            setWalkingDataBatch(batch, walkingDataRef, distance, elapsedTime, todayWithTime)
            badgeRepository.setBadgesBatch(batch, badgesRef, distance, newDistanceTotal, badgeData)
            userRepository.updateUserXp(batch, userId, distance)
            batch.commit()
        } catch (e: Exception) {
            Log.e("WalkRepository", "Error: ${e.message}", e)
            throw e
        }
    }

    private fun setWalkingDataBatch(
        batch: WriteBatch,
        walkingDataRef: DocumentReference,
        distance: Int,
        elapsedTime: Long,
        todayString: String
    ) {
        try {
            val walkingData = mapOf(
                "distance" to distance, // Metros
                "time" to elapsedTime, // Milliseconds
                "date" to todayString
            )
            batch.set(walkingDataRef, walkingData)
        } catch (e: Exception) {
            Log.e("WalkRepository", "Error: ${e.message}", e)
            throw e
        }
    }

    suspend fun getWalkHistory(
        userId: String,
        limit: Long,
        lastDocument: DocumentSnapshot? = null
    ): Pair<List<WalkHistoryItem>, DocumentSnapshot?> {
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
                val distance = document.getLong("distance")?.toInt() ?: 0
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
            Log.e("WalkRepository", "Error fetching walk history", e)
            throw e
        }
    }
}