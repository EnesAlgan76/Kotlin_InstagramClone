package com.example.kotlininstagramapp.api.model

class HomePagePostItem(
    val postId: Double,
    val userId: String,
    val userFullName: String,
    val userProfileImage: String,
    val content: String,
    val creationDate: String,
    var likeCount: Double,
    val postDescription: String,
    var userName: String
) {
    companion object {
        fun fromMap(map: Map<String, Any>): HomePagePostItem {
            val postId = map["postId"] as Double
            val userId = map["userId"] as String
            val userFullName = map["userFullName"] as String
            val userProfileImage = map["userProfileImage"] as String
            val content = map["content"] as String
            val creationDate = map["creationDate"] as String
            val likeCount = map["likeCount"] as Double
            val postDescription = map["postDescription"] as String
            val userName =map["userName"] as String

            return HomePagePostItem(
                postId,
                userId,
                userFullName,
                userProfileImage,
                content,
                creationDate,
                likeCount,
                postDescription,
                userName
            )
        }
    }
}
