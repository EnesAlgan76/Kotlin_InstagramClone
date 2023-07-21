package com.example.kotlininstagramapp.Models

class UserDetails(
    var follower: String,
    var following: String,
    var post: String,
    var profilePicture: String,
    var biography: String
) {
    companion object {
        fun fromMap(data: Map<String, Any>): UserDetails {
            val follower = data["follower"] as? String ?: ""
            val following = data["following"] as? String ?: ""
            val post = data["post"] as? String ?: ""
            val profilePicture = data["profilePicture"] as? String ?: ""
            val biography = data["biography"] as? String ?: ""

            return UserDetails(follower, following, post, profilePicture, biography)
        }
    }
}
