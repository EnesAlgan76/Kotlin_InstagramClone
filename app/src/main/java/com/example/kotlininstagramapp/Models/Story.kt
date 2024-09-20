package com.example.kotlininstagramapp.Models

import java.sql.Timestamp

class Story(
    val username: String,
    val fullName: String,
    val profilePicture: String,
    val stories: List<SingleStory>
)


class SingleStory (
    val storyId: Int,
    val storyImage: String,
    val creationDate: Long
)


