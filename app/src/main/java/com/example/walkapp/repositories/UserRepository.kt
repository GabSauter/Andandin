package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.Badge
import com.example.walkapp.models.DistanceDay
import com.example.walkapp.models.DistanceMonth
import com.example.walkapp.models.Performance
import com.example.walkapp.models.User
import com.example.walkapp.views.historicscreen.WalkHistoryItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction
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
                val performance = getPerformanceData(transaction, performanceDataRef)
                val badgeData = getBadgeData(transaction, badgesRef)

                val newDistanceTotal = performance.distanceTotal + distance
                setPerformanceData(transaction, performanceDataRef, performance, distance, newDistanceTotal, todayString, currentMonth)
                setWalkingData(transaction, walkingDataRef, distance, elapsedTime, todayString)
                setBadges(transaction, badgesRef, distance, newDistanceTotal, badgeData)
                null
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getPerformanceData(transaction: Transaction, performanceDataRef: DocumentReference): Performance {
        val snapshot = transaction.get(performanceDataRef)
        val performanceData = snapshot.data ?: emptyMap<String, Any>()

        return if (performanceData.isNotEmpty()) {
            Performance.mapToPerformance(performanceData)
        } else {
            Performance(
                distanceTotal = 0.0,
                distanceLast7Days = emptyList(),
                distanceLast12Months = emptyList()
            )
        }
    }

    private fun setPerformanceData(transaction: Transaction, performanceDataRef: DocumentReference, performance: Performance, distance: Double, newDistance: Double, todayString: String, currentMonth: String){
        val updatedDistanceLast7Days = updateDistanceLast7Days(performance, distance, todayString)
        val updatedDistanceLast12Months = updateDistanceLast12Months(performance, distance, currentMonth)

        val updatedPerformance = Performance(
            distanceTotal = newDistance,
            distanceLast7Days = updatedDistanceLast7Days,
            distanceLast12Months = updatedDistanceLast12Months
        )

        transaction.set(performanceDataRef, updatedPerformance.toMap(), SetOptions.merge())
    }

    private fun updateDistanceLast7Days(performance: Performance, distance: Double, todayString: String): List<DistanceDay> {
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

        return updatedDistanceLast7Days
    }

    private fun updateDistanceLast12Months(performance: Performance, distance: Double, currentMonth: String): List<DistanceMonth> {
        val updatedDistanceLast12Months = performance.distanceLast12Months.toMutableList()
        val monthEntry = updatedDistanceLast12Months.find { it.month == currentMonth }
        if (monthEntry != null) {
            val updatedMonthEntry = monthEntry.copy(distance = monthEntry.distance + distance)
            updatedDistanceLast12Months[updatedDistanceLast12Months.indexOf(monthEntry)] =
                updatedMonthEntry
        } else {
            updatedDistanceLast12Months.add(
                DistanceMonth(
                    distance = distance,
                    month = currentMonth
                )
            )
        }

        return updatedDistanceLast12Months
    }

    private fun setWalkingData(transaction: Transaction, walkingDataRef: DocumentReference, distance: Double, elapsedTime: Long, todayString: String){
        val walkingData = mapOf(
            "distance" to distance,
            "time" to elapsedTime,
            "date" to todayString
        )
        transaction.set(walkingDataRef, walkingData)
    }

    private fun setBadges(transaction: Transaction, badgeRef: DocumentReference, distance: Double, newDistanceTotal: Double, badgeData: Badge) {
        //Total distance Badges:
        if(newDistanceTotal >= 1.000) { //distancia de 1 km
            badgeData.badge1 = true
        }
        if(newDistanceTotal >= 5.000) {//distancia de 5 km
            badgeData.badge2 = true
        }

        if(newDistanceTotal >= 10.000) {//distancia de 10 km
            badgeData.badge3 = true
        }
        if(newDistanceTotal >= 25.000) {//distancia de 25 km
            badgeData.badge4 = true
        }
        if(newDistanceTotal >= 50.000) {//distancia de 50 km
            badgeData.badge5 = true
        }
        if(newDistanceTotal >= 100.000) {//distancia de 100 km
            badgeData.badge6 = true
        }

        //One walk Badges:
        if(distance >= 4.000) {//distancia de 4 km no mesmo dia
            badgeData.badge7 = true
        }
        if(distance >= 8.000) {//distancia de 8 km no mesmo dia
            badgeData.badge8 = true
        }
        if(distance >= 16.000) {//distancia de 16 km no mesmo dia
            badgeData.badge9 = true
        }

        transaction.set(badgeRef, badgeData, SetOptions.merge())
    }

    private fun getBadgeData(transaction: Transaction, badgeRef: DocumentReference): Badge {
        val snapshot = transaction.get(badgeRef)
        val badgeData = snapshot.data ?: emptyMap<String, Any>()
        if(badgeData.isEmpty()) {
            return Badge(
                badge1 = false,
                badge2 = false,
                badge3 = false,
                badge4 = false,
                badge5 = false,
                badge6 = false,
                badge7 = false,
                badge8 = false,
                badge9 = false
            )
        }
        val badges = Badge.mapToBadge(badgeData as Map<String, Any>)
        return badges
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
