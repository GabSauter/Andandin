package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.DistanceDay
import com.example.walkapp.models.DistanceMonth
import com.example.walkapp.models.Performance
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class PerformanceRepository {
    private val db = Firebase.firestore

    suspend fun getPerformanceData(userId: String): Performance {
        try {
            val document = db.collection("users")
                .document(userId)
                .collection("performanceData")
                .document("performance")
                .get()
                .await()

            if (!document.exists() || document.data == null) {
                return Performance(
                    distanceTotal = 0,
                    distanceLast7Days = emptyList(),
                    distanceLast12Months = emptyList()
                )
            }
            return Performance.mapToPerformance(document.data!!)
        } catch (e: Exception) {
            Log.e("PerformanceRepository", "Error getting performance data", e)
            throw e
        }
    }

    fun getPerformanceDataBatch(transaction: Transaction, performanceDataRef: DocumentReference): Performance {
        val snapshot = transaction.get(performanceDataRef)
        val performanceData = snapshot.data ?: emptyMap<String, Any>()

        return if (performanceData.isNotEmpty()) {
            Performance.mapToPerformance(performanceData)
        } else {
            Performance(
                distanceTotal = 0,
                distanceLast7Days = emptyList(),
                distanceLast12Months = emptyList()
            )
        }
    }

    fun setPerformanceDataBatch(batch: WriteBatch, performanceDataRef: DocumentReference, performance: Performance, distance: Int, newDistance: Int, todayString: String, currentMonth: String){
        val updatedDistanceLast7Days = updateDistanceLast7Days(performance, distance, todayString)
        val updatedDistanceLast12Months = updateDistanceLast12Months(performance, distance, currentMonth)

        val updatedPerformance = Performance(
            distanceTotal = newDistance,
            distanceLast7Days = updatedDistanceLast7Days,
            distanceLast12Months = updatedDistanceLast12Months
        )

        batch.set(performanceDataRef, updatedPerformance.toMap(), SetOptions.merge())
    }

    private fun updateDistanceLast7Days(performance: Performance, distance: Int, todayString: String): List<DistanceDay> {
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

    private fun updateDistanceLast12Months(performance: Performance, distance: Int, currentMonth: String): List<DistanceMonth> {
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
}