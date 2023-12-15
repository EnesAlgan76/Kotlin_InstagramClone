package com.example.kotlininstagramapp.Models

import com.google.firebase.Timestamp

class ChatMessage(var message: String, var receiver_id: String, var sender_id: String, var timestamp: Timestamp?) {

    companion object {
        fun fromMap(map: Map<String, Any>): ChatMessage {
            val message = map["message"] as String
            val receiverId = map["receiver_id"] as String
            val senderId = map["sender_id"] as String
            val timestamp = map["timestamp"] as Timestamp?
            return ChatMessage(message, receiverId, senderId, timestamp)
        }
    }
}
