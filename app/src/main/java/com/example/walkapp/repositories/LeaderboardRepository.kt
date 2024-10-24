package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.LeaderboardUser
import com.example.walkapp.models.LeaderboardUser.Companion.emptyLeaderboardUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class LeaderboardRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getLeaderboardForMonth(
        monthAndYear: String
    ): List<LeaderboardUser> {
        try {
            val querySnapshot = db.collection("leaderboards")
                .whereEqualTo("monthAndYear", monthAndYear)
                .orderBy("distance", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.d("LeaderboardRepository", "No documents found for month: $monthAndYear")
                return emptyList()
            }
            Log.d("LeaderboardRepository", "Documents found for month: $monthAndYear")
            return querySnapshot.documents.mapNotNull { doc ->
                LeaderboardUser.mapToLeaderboardUser(doc)
            }
        } catch (e: Exception) {
            Log.e("LeaderboardRepository", "Error getting leaderboard for month", e)
            throw e
        }
    }

    suspend fun getLeaderboardForMonthInGroup(
        monthAndYear: String,
        userId: String
    ): List<LeaderboardUser> {
        try {
            val userSnapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (userSnapshot == null || !userSnapshot.exists() || userSnapshot["group"] == null) {
                return emptyList()
            }

            val querySnapshot = db.collection("leaderboards")
                .whereEqualTo("monthAndYear", monthAndYear)
                .whereEqualTo("group", userSnapshot["group"])
                .orderBy("distance", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                return emptyList()
            }
            return querySnapshot.documents.mapNotNull { doc ->
                LeaderboardUser.mapToLeaderboardUser(doc)
            }
        } catch (e: Exception) {
            Log.e("LeaderboardRepository", "Error getting leaderboard for month in group", e)
            throw e
        }
    }

    suspend fun getUserLeaderboard(userId: String, monthAndYear: String): LeaderboardUser {
        try {
            val querySnapshot = db.collection("leaderboards")
                .document(userId)
                .get()
                .await()

            if (querySnapshot == null || !querySnapshot.exists() || querySnapshot.data == null || monthAndYear != querySnapshot.data!!["monthAndYear"]) {
                return emptyLeaderboardUser()
            }
            return LeaderboardUser.mapToLeaderboardUser(querySnapshot.data!!)
        } catch (e: Exception) {
            Log.e("LeaderboardRepository", "Error getting user leaderboard", e)
            throw e
        }
    }
}