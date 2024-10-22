package com.example.walkapp.models

class GroupUser(
    val avatarIndex: Int,
    val nickname: String,
    //val distanceWalked: String
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "avatarIndex" to avatarIndex,
            "nickname" to nickname,
            //"distanceWalked" to distanceWalked
        )
    }

    companion object{
        fun mapToGroupUser(data: Map<String, Any>): GroupUser {
            return GroupUser(
                avatarIndex = (data["avatarIndex"] as Long).toInt(),
                nickname = data["nickname"] as String
                //distanceWalked = data["distanceWalked"] as String
            )
        }
    }
}