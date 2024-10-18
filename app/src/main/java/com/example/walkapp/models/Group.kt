package com.example.walkapp.models

class Group(val name: String, val password: String) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "password" to password
        )
    }

    companion object{
        fun mapToGroup(data: Map<String, Any>): Group {
            return Group(
                name = data["name"] as String,
                password = data["password"] as String
            )
        }
    }
}