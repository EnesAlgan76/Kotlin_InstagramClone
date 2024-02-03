package com.example.kotlininstagramapp.Models

import java.sql.Timestamp

class Story(
    var userId: String,
    var userName: String,
    var userProfilePicture: String,
    val stories: MutableList<SingleStory> = mutableListOf()
)


class SingleStory (
    var storyId: String,
    var url: String,
    val timestamp: com.google.firebase.Timestamp,
)


