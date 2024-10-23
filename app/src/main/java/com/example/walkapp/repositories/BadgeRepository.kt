package com.example.walkapp.repositories

import android.util.Log
import com.example.walkapp.models.Badges
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class BadgeRepository {
    private val db = Firebase.firestore

    suspend fun getBadges(userId: String): Badges {
        try{
            val badgeRef = db.collection("users")
                .document(userId).collection("badgeData")
                .document("badge")

            val snapshot = badgeRef.get().await()
            if(snapshot.exists()){
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
            }else{
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
        }catch (e: Exception){
            Log.e("UserRepository", "Error getting badges", e)
            throw e
        }
    }

    fun setBadgesBatch(batch: WriteBatch, badgeRef: DocumentReference, distance: Int, newDistanceTotal: Int, badgeData: Badges) {
        //Total distance Badges:
        if(newDistanceTotal >= 1000) { //distancia de 1 km
            badgeData.badge1 = true
        }
        if(newDistanceTotal >= 5000) {//distancia de 5 km
            badgeData.badge2 = true
        }

        if(newDistanceTotal >= 10000) {//distancia de 10 km
            badgeData.badge3 = true
        }
        if(newDistanceTotal >= 25000) {//distancia de 25 km
            badgeData.badge4 = true
        }
        if(newDistanceTotal >= 50000) {//distancia de 50 km
            badgeData.badge5 = true
        }
        if(newDistanceTotal >= 100000) {//distancia de 100 km
            badgeData.badge6 = true
        }

        //One walk Badges:
        if(distance >= 4000) {//distancia de 4 km no mesmo dia
            badgeData.badge7 = true
        }
        if(distance >= 8000) {//distancia de 8 km no mesmo dia
            badgeData.badge8 = true
        }
        if(distance >= 16000) {//distancia de 16 km no mesmo dia
            badgeData.badge9 = true
        }

        batch.set(badgeRef, badgeData, SetOptions.merge())
    }
}