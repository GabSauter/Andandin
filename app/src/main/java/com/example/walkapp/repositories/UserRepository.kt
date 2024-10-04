package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.DistanceDay
import com.example.walkapp.models.DistanceMonth
import com.example.walkapp.models.Performance
import com.example.walkapp.models.User
import com.example.walkapp.views.historicscreen.WalkHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    suspend fun saveWalkingData(userId: String, distance: Double, elapsedTime: Long) {
        try {
            val calendar = Calendar.getInstance()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val todayString = dateFormat.format(calendar.time)
            val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            Log.d("UserRepository", "Current week: $currentWeek")
            val monthFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val currentMonth = monthFormat.format(calendar.time)

            val userRef = db.collection("users").document(userId)
            val walkingDataRef = userRef.collection("walkingData").document()
            val performanceDataRef = userRef.collection("performanceData").document("performance")

            db.runTransaction { transaction ->

                val snapshot = transaction.get(performanceDataRef)
                val performanceData = snapshot.data ?: emptyMap<String, Any>()

                val performance = if (performanceData.isNotEmpty()) {
                    Performance.mapToPerformance(performanceData)
                } else {
                    Performance(
                        distanceTotal = 0.0,
                        distanceLast7Days = emptyList(),
                        distanceLast12Months = emptyList()
                    )
                }

                val newDistanceTotal = performance.distanceTotal + distance

                val updatedDistanceLast7Days = performance.distanceLast7Days.toMutableList()
                val todayEntry = updatedDistanceLast7Days.find { it.day == todayString }
                if (todayEntry != null) {
                    val updatedTodayEntry = todayEntry.copy(distance = todayEntry.distance + distance)
                    updatedDistanceLast7Days[updatedDistanceLast7Days.indexOf(todayEntry)] = updatedTodayEntry
                } else {
                    updatedDistanceLast7Days.add(DistanceDay(distance = distance, day = todayString))
                }

                if (updatedDistanceLast7Days.size > 7) {
                    updatedDistanceLast7Days.removeAt(0)
                }

                val updatedDistanceLast12Months = performance.distanceLast12Months.toMutableList()
                val monthEntry = updatedDistanceLast12Months.find { it.month == currentMonth }
                if (monthEntry != null) {
                    val updatedMonthEntry = monthEntry.copy(distance = monthEntry.distance + distance)
                    updatedDistanceLast12Months[updatedDistanceLast12Months.indexOf(monthEntry)] = updatedMonthEntry
                } else {
                    updatedDistanceLast12Months.add(DistanceMonth(distance = distance, month = currentMonth))
                }

                val updatedPerformance = Performance(
                    distanceTotal = newDistanceTotal,
                    distanceLast7Days = updatedDistanceLast7Days,
                    distanceLast12Months = updatedDistanceLast12Months
                )

                transaction.set(performanceDataRef, updatedPerformance.toMap(), SetOptions.merge())

                val walkingData = mapOf(
                    "distance" to distance,
                    "time" to elapsedTime,
                    "date" to todayString
                )
                transaction.set(walkingDataRef, walkingData)

                null
            }.await()

        } catch (e: Exception) {
            throw e
        }
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

            Log.d("UserRepository", "Walk history items: $walkHistoryItems")

            val lastVisibleDocument = querySnapshot.documents.lastOrNull()

            return Pair(walkHistoryItems, lastVisibleDocument)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching walk history", e)
            throw e
        }
    }
}