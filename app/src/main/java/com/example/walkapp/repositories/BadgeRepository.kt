package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.Badges
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction
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

    fun setBadges(transaction: Transaction, badgeRef: DocumentReference, distance: Double, newDistanceTotal: Double, badgeData: Badges) {
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

    fun getBadgeData(transaction: Transaction, badgeRef: DocumentReference): Badges {
        val snapshot = transaction.get(badgeRef)
        val badgeData = snapshot.data ?: emptyMap<String, Any>()
        if(badgeData.isEmpty()) {
            return Badges(
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
        val badges = Badges.mapToBadge(badgeData as Map<String, Any>)
        return badges
    }
}