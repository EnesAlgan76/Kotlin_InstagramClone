package com.example.kotlininstagramapp.Models

import com.google.firebase.Timestamp

data class Conversation(
    var user_id: String,
    var last_message: String,
    var last_view: Timestamp?,
    var isRead: Boolean,
    var conversation_id: String,
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Conversation {
            val userId = map["user_id"] as? String ?: "null"
            val lastMessage = map["last_message"] as? String ?: "null"
            val lastView = map["last_view"] as Timestamp?
            val conversationId = map["conversation_id"] as? String ?: "null"
            val isRead = map["is_read"] as? Boolean ?: false

            return Conversation(userId,lastMessage, lastView ,isRead, conversationId)
        }
    }

    override fun toString(): String {

        return "(lastMessage: ${last_message}, isRead: ${isRead})"
    }
}

