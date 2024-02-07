package com.example.kotlininstagramapp.Models

class UserDetails(
    var follower: Int,
    var following: Int,
    var post: Int,
    var profilePicture: String,
    var biography: String
) {

    constructor() : this(0,0,0,"ımage","")


    companion object {
        fun fromMap(data: Map<String, Any>): UserDetails {
            val follower = (data["follower"] as Long).toInt()
            val following = (data["following"] as Long).toInt()
            val post = (data["post"] as Long).toInt()
            val profilePicture = data["profilePicture"] as? String ?: ""
            val biography = data["biography"] as? String ?: ""

            val finalProfilePicture = if (profilePicture.isBlank()) "resim bulunamadi" else profilePicture

            return UserDetails(follower, following, post, finalProfilePicture, biography)
        }
    }
}
