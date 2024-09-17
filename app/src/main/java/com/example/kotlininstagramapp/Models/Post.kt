package com.example.kotlininstagramapp.Models

class Post {
    val userId: String
    val postId: Double
    val creationDate: Long
    val explanation: String
    val content: String

    constructor(
        userId: String,
        postId: Double,
        date: Long,
        explanation: String,
        url: String
    ) {
        this.userId = userId
        this.postId = postId
        this.creationDate = date
        this.explanation = explanation
        this.content = url
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Post {
            val userId = map["userId"] as String
            val postId = map["postId"] as Double
            val date = map["creationDate"] as Long
            val explanation = map["explanation"] as String
            val url = map["content"] as String

            return Post(userId, postId, date, explanation, url)
        }
    }


    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "creationDate" to creationDate,
            "explanation" to explanation,
            "content" to content
        )
    }



}
