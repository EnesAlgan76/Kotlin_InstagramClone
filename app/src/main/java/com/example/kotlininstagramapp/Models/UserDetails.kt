package com.example.kotlininstagramapp.Models

class UserDetails(
    var follower: String,
    var following: String,
    var post: String,
    var profilePicture: String,
    var biography: String
) {

    constructor() : this("","","","ımage","")


    companion object {
        fun fromMap(data: Map<String, Any>): UserDetails {
            val follower = data["follower"] as? String ?: ""
            val following = data["following"] as? String ?: ""
            val post = data["post"] as? String ?: ""
            val profilePicture = data["profilePicture"] as? String ?: ""
            val biography = data["biography"] as? String ?: ""

            val finalProfilePicture = if (profilePicture.isBlank()) "resim bulunamadi" else profilePicture

            return UserDetails(follower, following, post, finalProfilePicture, biography)
        }
    }
}
