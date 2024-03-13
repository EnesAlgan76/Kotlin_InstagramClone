package com.example.kotlininstagramapp.api.model

data class NotificationModel(
    val id: Double,
    val userId: String,
    val type: String,
    val time: String,
    val postPreview: String?,
    val fromUserId: String,
    val fromUserProfilePicture: String,
    val fromUserName: String
) {
    companion object {
        fun fromMap(map: Map<String, Any>): NotificationModel {
            val id = map["id"] as Double
            val userId = map["userId"] as String
            val type = map["type"] as String
            val time = map["time"] as String
            val fromUserId = map["fromUserId"] as String
            val fromUserProfilePicture = map["fromUserProfilePicture"] as String
            val fromUserName = map["fromUserName"] as String
            val postPreview = map["postPreview"] as? String

            return NotificationModel(id, userId, type, time, postPreview, fromUserId, fromUserProfilePicture, fromUserName)
        }
    }
}
