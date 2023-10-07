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



}
