package com.example.kotlininstagramapp.Models

import java.sql.Timestamp

class Story(
    var storyId: String,
    var userId: String,
    var userName: String,
    var userProfilePicture: String,
    var url: String,
    val timestamp: com.google.firebase.Timestamp,
) {
    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["userId"] = userId
        map["userName"] = userName
        map["userProfilePicture"] = userProfilePicture
        map["url"] = url
        map["timestamp"] = timestamp
        return map
    }
}
