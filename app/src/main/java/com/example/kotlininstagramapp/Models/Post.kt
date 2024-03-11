package com.example.kotlininstagramapp.Models

class Post {
    val userId: String
    val postId: Double
    val date: String
    val explanation: String
    val url: String

    constructor(
        userId: String,
        postId: Double,
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
            val postId = map["postId"] as Double
            val date = map["creationDate"] as String
            val explanation = map["explanation"] as String
            val url = map["content"] as String

            return Post(userId, postId, date, explanation, url)
        }
    }


    fun toMap(): Map<String, String> {
        return mapOf(
            "userId" to userId,
            "creationDate" to date,
            "explanation" to explanation,
            "content" to url
        )
    }



}
