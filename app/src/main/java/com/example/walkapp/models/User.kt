package com.example.walkapp.models

data class User(
    var id: String = "",
    val nickname: String = "",
    val xp: Int = 0,
    val walkingGoal: Int = 0,
    val avatarIndex: Int = 0,
    var fcmToken: String = ""
) {
    fun createUser(): Map<String, Any> {
        return mapOf(
            "nickname" to nickname,
            "xp" to xp,
            "walkingGoal" to walkingGoal,
            "avatarIndex" to avatarIndex,
            "fcmToken" to fcmToken
        )
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "nickname" to nickname,
            "xp" to xp,
            "walkingGoal" to walkingGoal,
            "avatarIndex" to avatarIndex
        )
    }

    fun toMapUpdateUser(): Map<String, Any> {
        return mapOf(
            "nickname" to nickname,
            "walkingGoal" to walkingGoal,
            "avatarIndex" to avatarIndex
        )
    }

    fun toMapUpdateXp(): Map<String, Any> {
        return mapOf(
            "xp" to xp
        )
    }

    companion object {
        fun mapToUser(id: String, data: Map<String, Any>): User {
            return User(
                id = id,
                nickname = data["nickname"] as String,
                xp = (data["xp"] as Long).toInt(),
                walkingGoal = (data["walkingGoal"] as Long).toInt(),
                avatarIndex = (data["avatarIndex"] as Long).toInt()
            )
        }
    }

    fun isEmpty(): Boolean {
        return nickname.isBlank() && xp == 0 && walkingGoal == 0 && avatarIndex == 0
    }
}