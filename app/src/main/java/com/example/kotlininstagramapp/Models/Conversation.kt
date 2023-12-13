package com.example.kotlininstagramapp.Models

data class Conversation(
    val user_id: String,
    val profile_image: String,
    val user_full_name: String,
    val last_message: String,
    var conversation_id: String
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Conversation {
            val userId = map["user_id"] as? String ?: "null"
            val profile_image = map["profile_image"] as? String ?: "null"
            val userFullName = map["user_full_name"] as? String ?: "null"
            val lastMessage = map["last_message"] as? String ?: "null"
            val conversationId = map["conversation_id"] as? String ?: "null"

            return Conversation(userId, profile_image, userFullName, lastMessage, conversationId)
        }
    }
}

