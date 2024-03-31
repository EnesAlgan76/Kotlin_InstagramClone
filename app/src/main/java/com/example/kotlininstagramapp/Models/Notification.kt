package com.example.kotlininstagramapp.Models

class Notification(
    val id: Double,
    val userId: String,
    val profileImage: String,
    val userName :String,
    val type: String,
    val time: String,
    val postPreview: String?,
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Notification {
            val id = map["id"] as Double
            val userId = map["userId"] as String
            val profileImage = map["profileImage"] as String
            val userName = map["userName"] as String
            val type = map["type"] as String
            val timestamp = map["time"] as String
            val postPreview = map["postPreview"] as String

            return Notification(id,userId, profileImage, userName , type, timestamp, postPreview)
        }
    }
}