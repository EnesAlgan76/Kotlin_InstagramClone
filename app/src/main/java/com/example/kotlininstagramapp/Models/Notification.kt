package com.example.kotlininstagramapp.Models

import java.sql.Timestamp

class Notification(
    val id: String,
    val profileImage: String,
    val userName :String,
    val type: String,
    val timestamp: com.google.firebase.Timestamp,
    val postPreview: String?,
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Notification {
            val id = map["id"] as String
            val profileImage = map["profile_image"] as String
            val userName = map["user_name"] as String
            val type = map["type"] as String
            val timestamp = map["timestamp"] as com.google.firebase.Timestamp
            val postPreview = map["post_preview"] as String

            return Notification(id,profileImage, userName , type, timestamp, postPreview)
        }
    }
}