package com.example.walkapp.models

import com.google.firebase.firestore.DocumentSnapshot

class GroupUserWalk(
    val avatarIndex: Int,
    val nickname: String,
    val distanceWalked: Int,
    val date: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "avatarIndex" to avatarIndex,
            "nickname" to nickname,
            "distanceWalked" to distanceWalked,
            "date" to date
        )
    }

    companion object {
        fun mapToGroupUserWalks(data: DocumentSnapshot): GroupUserWalk {
            return GroupUserWalk(
                avatarIndex = (data["avatarIndex"] as Long).toInt(),
                nickname = data["nickname"] as String,
                distanceWalked = (data["distanceWalked"] as Long).toInt(),
                date = data["date"] as String
            )
        }
    }
}