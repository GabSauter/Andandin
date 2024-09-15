package com.example.walkapp.models

data class User(
    var id: String = "",
    val nickname: String = "",
    val dateOfBirth: String = "",
    val walksRegularly: Boolean = false,
    val walkingGoal: String = "",
    val avatarIndex: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nickname" to nickname,
            "dateOfBirth" to dateOfBirth,
            "walksRegularly" to walksRegularly,
            "walkingGoal" to walkingGoal,
            "avatarIndex" to avatarIndex
        )
    }

    companion object {
        fun mapToUser(id: String, data: Map<String, Any>): User {
            return User(
                id = id,
                nickname = data["nickname"] as String,
                dateOfBirth = data["dateOfBirth"] as String,
                walksRegularly = data["walksRegularly"] as Boolean,
                walkingGoal = data["walkingGoal"] as String,
                avatarIndex = (data["avatarIndex"] as Long).toInt()
            )
        }
    }

    fun isEmpty(): Boolean {
        return nickname.isBlank() && dateOfBirth.isBlank() && walkingGoal.isBlank()
    }
}