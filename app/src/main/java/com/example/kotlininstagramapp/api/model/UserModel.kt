package com.example.kotlininstagramapp.api.model

data class UserModel(
    val userId: String,
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
    var postCount: Int
) {
    companion object {
        fun fromMap(map: Map<String, Any>): UserModel {
            return UserModel(
                userId = map["userId"] as String,
                userName = map["userName"] as String,
                password = map["password"] as String,
                phoneNumber = map["phoneNumber"] as String,
                email = map["email"] as String,
                fullName = map["fullName"] as String,
                profilePicture = map["profilePicture"] as String,
                biography = map["biography"] as String,
                fcmToken = map["fcmToken"] as? String?:"",
                followerCount = (map["followerCount"] as Double).toInt(),
                followingCount = (map["followingCount"] as Double).toInt(),
                postCount = (map["postCount"] as Double).toInt()
            )
        }
    }
}
