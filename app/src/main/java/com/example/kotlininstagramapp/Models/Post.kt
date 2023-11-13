package com.example.kotlininstagramapp.Models

class Post {
    val userId: String
    val postId: String
    val date: String
    val explanation: String
    val url: String

    constructor(
        userId: String,
        postId: String,
        date: String,
        explanation: String,
        url: String
    ) {
        this.userId = userId
        this.postId = postId
        this.date = date
        this.explanation = explanation
        this.url = url
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Post {
            val userId = map["userId"] as String
            val postId = map["postId"] as String
            val date = map["date"] as String
            val explanation = map["explanation"] as String
            val url = map["url"] as String

            return Post(userId, postId, date, explanation, url)
        }
    }



}
