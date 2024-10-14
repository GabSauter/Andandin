package com.example.walkapp.models

import com.google.firebase.firestore.DocumentSnapshot

class LeaderboardUser(
    val nickname: String,
    val distance: Double,
    val avatarIndex: Int,
    val monthAndYear: String,
    val group: String? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nickname" to nickname,
            "distance" to distance,
            "monthAndYear" to monthAndYear,
            "avatarIndex" to avatarIndex,
            "group" to (group ?: "")
        )
    }

    companion object{
        fun mapToLeaderboardUser(data: DocumentSnapshot): LeaderboardUser{
            if(data["group"] != null){
                return LeaderboardUser(
                    nickname = data["nickname"] as String,
                    distance = data["distance"] as Double,
                    monthAndYear = data["monthAndYear"] as String,
                    avatarIndex = data["avatarIndex"] as Int,
                    group = data["group"] as String
                )
            }else{
                return LeaderboardUser(
                    nickname = data["nickname"] as String,
                    distance = data["distance"] as Double,
                    monthAndYear = data["monthAndYear"] as String,
                    avatarIndex = (data["avatarIndex"] as Long).toInt()
                )
            }
        }

        fun mapToLeaderboardUser(data: Map<String, Any>): LeaderboardUser{
            if(data["group"] != null){
                return LeaderboardUser(
                    nickname = data["nickname"] as String,
                    distance = data["distance"] as Double,
                    monthAndYear = data["monthAndYear"] as String,
                    avatarIndex = data["avatarIndex"] as Int,
                    group = data["group"] as String
                )
            }else{
                return LeaderboardUser(
                    nickname = data["nickname"] as String,
                    distance = data["distance"] as Double,
                    monthAndYear = data["monthAndYear"] as String,
                    avatarIndex = data["avatarIndex"] as Int
                )
            }
        }

        fun emptyLeaderboardUser(): LeaderboardUser{
            return LeaderboardUser(
                nickname = "",
                distance = 0.0,
                avatarIndex = 0,
                monthAndYear = ""
            )
        }
    }

    fun isEmpty(): Boolean{
        return nickname.isEmpty() && distance == 0.0 && avatarIndex == 0 && monthAndYear.isEmpty()
    }
}