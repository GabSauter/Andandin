package com.example.walkapp.repositories

import com.example.walkapp.exceptions.GroupDoesNotExistException
import com.example.walkapp.exceptions.GroupNameAlreadyExistsException
import com.example.walkapp.exceptions.IncorrectGroupNameOrPasswordException
import com.example.walkapp.models.Group
import com.example.walkapp.models.GroupUser
import com.example.walkapp.models.GroupUserWalk
import com.example.walkapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
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

    suspend fun leaveGroup(userId: String) {
        try {
            val userRef = db.collection("users").document(userId)
            val userSnapshot = userRef.get().await()

            if (userSnapshot.exists() && userSnapshot.contains("group")) {
                val groupName = userSnapshot.getString("group") ?: throw GroupDoesNotExistException()

                val groupUserRef = db.collection("groups").document(groupName)
                    .collection("users").document(userId)
                groupUserRef.delete().await()

                userRef.update(mapOf("group" to null)).await()
            } else {
                throw GroupDoesNotExistException()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getUserGroup(userId: String): Group? {
        try {
            val userRef = db.collection("users").document(userId)
            val userSnapshot = userRef.get().await()

            if (userSnapshot.exists() && userSnapshot.contains("group")) {
                val groupName = userSnapshot.getString("group") ?: return null

                val groupRef = db.collection("groups").document(groupName)
                val groupSnapshot = groupRef.get().await()

                return if (groupSnapshot.exists() && groupSnapshot.data != null) {
                    Group.mapToGroup(groupSnapshot.data!!)
                } else {
                    null
                }
            }
            return null
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getGroupUsers(groupName: String): List<GroupUser> {
        try {
            val groupUsersRef = db.collection("groups").document(groupName).collection("users")
            val usersSnapshot = groupUsersRef.get().await()

            if (!usersSnapshot.isEmpty) {
                val usersList = mutableListOf<GroupUser>()
                for (document in usersSnapshot.documents) {
                    val user = GroupUser.mapToGroupUser(document.data!!)
                    user.let { usersList.add(it) }
                }
                return usersList
            }
            return emptyList()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getPaginatedUserWalks(groupName: String, lastDocument: DocumentSnapshot? = null): Pair<List<GroupUserWalk>, DocumentSnapshot?> {
        try{
            var query = db.collection("groups")
                .document(groupName)
                .collection("userWalks")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(8)

            if (lastDocument != null) {
                query = query.startAfter(lastDocument)
            }

            val querySnapshot = query.get().await()
            if (querySnapshot.isEmpty) {
                return Pair(emptyList(), null)
            }

            val userWalksItems = querySnapshot.documents.mapNotNull { document ->
                val distanceWalked = document.getLong("distanceWalked")?.toInt() ?: 0
                val date = document.getString("date").toString()
                val avatarIndex = document.getLong("avatarIndex")?.toInt() ?: 0
                val nickname = document.getString("nickname").toString()

                GroupUserWalk(
                    distanceWalked = distanceWalked,
                    date = date,
                    avatarIndex = avatarIndex,
                    nickname = nickname
                )
            }
            val lastVisibleDocument = querySnapshot.documents.lastOrNull()
            return Pair(userWalksItems, lastVisibleDocument)
        }catch (e: Exception){
            throw e
        }
    }
}