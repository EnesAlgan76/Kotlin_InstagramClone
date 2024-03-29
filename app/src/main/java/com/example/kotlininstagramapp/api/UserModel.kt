package com.example.kotlininstagramapp.api

data class UserModel(
    val userId :String,
    val userName: String,
    val password: String,
    val phoneNumber: String,
    val email: String,
    val fullName: String,
    val profilePicture: String,
    val biography: String,
    val fcmToken: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
)