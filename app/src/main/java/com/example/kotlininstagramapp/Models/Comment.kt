package com.example.kotlininstagramapp.Models

class Comment {
    var commentId:String
    var comment:String
    var like_count:String
    var user_id:String
    var user_profile_picture:String
    var time_ago:String
    var user_name:String

    constructor(
        commentId: String,
        comment: String,
        like_count: String,
        user_id: String,
        user_profile_picture: String,
        time_ago: String,
        user_name: String
    ) {
        this.commentId = commentId
        this.comment = comment
        this.like_count = like_count
        this.user_id = user_id
        this.user_profile_picture = user_profile_picture
        this.time_ago = time_ago
        this.user_name = user_name
    }


}