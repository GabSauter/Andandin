package com.example.walkapp.repositories

import com.example.walkapp.exceptions.GroupDoesNotExistException
import com.example.walkapp.exceptions.GroupNameAlreadyExistsException
import com.example.walkapp.exceptions.IncorrectGroupNameOrPasswordException
import com.example.walkapp.models.Group
import com.example.walkapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class GroupRepository {
    private val db = Firebase.firestore

    suspend fun createGroup(userId: String, group: Group, userData: User) {
        try {
            val groupDocRef = db.collection("groups").document(group.name)
            val groupSnapshot = groupDocRef.get().await()

            if (groupSnapshot.exists()) {
                throw GroupNameAlreadyExistsException()
            } else {
                val batch = db.batch()
                batch.set(groupDocRef, group.toMap())
                batch.set(groupDocRef.collection("users").document(userId),
                    mapOf("nickname" to userData.nickname, "avatarIndex" to userData.avatarIndex))
                batch.set(db.collection("users").document(userId),
                    mapOf("group" to group.name), SetOptions.merge())
                batch.commit().await()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun joinGroup(userId: String, group: Group, userData: User) {
        try {
            val groupSnapshot = db.collection("groups").document(group.name).get().await()
            if (!groupSnapshot.exists()) {
                throw GroupDoesNotExistException()
            }
            if (groupSnapshot["password"] != group.password) {
                throw IncorrectGroupNameOrPasswordException()
            }

            val batch = db.batch()
            val userInGroupRef = db.collection("groups").document(group.name)
                .collection("users").document(userId)
            batch.set(userInGroupRef, mapOf("nickname" to userData.nickname, "avatarIndex" to userData.avatarIndex))

            val userRef = db.collection("users").document(userId)
            batch.set(userRef, mapOf("group" to group.name), SetOptions.merge())
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun isUserPartOfGroup(userId: String): Boolean{
        try{
            val userSnapshot = db.collection("users").document(userId).get().await()
            return userSnapshot.exists() && userSnapshot["group"] != null
        }catch (e: Exception){
            throw e
        }
    }
}