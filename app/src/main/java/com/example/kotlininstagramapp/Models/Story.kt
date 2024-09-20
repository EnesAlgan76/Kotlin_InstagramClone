package com.example.kotlininstagramapp.Models


data class Story(
    val username: String,
    val fullName: String,
    val profilePicture: String,
    val stories: List<SingleStory>
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Story {
            val username = map["username"] as String
            val fullName = map["fullName"] as String
            val profilePicture = map["profilePicture"] as String
            val storiesData = map["stories"] as List<Map<String, Any>>
            val stories = storiesData.map { SingleStory.fromMap(it) }
            return Story(username, fullName, profilePicture, stories)
        }
    }
}

data class SingleStory(
    val storyId: Double,
    val storyImage: String,
    val creationDate: Long
) {
    companion object {
        fun fromMap(map: Map<String, Any>): SingleStory {
            val storyId = map["storyId"] as Double
            val storyImage = map["storyImage"] as String
            val creationDate = (map["creationDate"] as Double).toLong()
            return SingleStory(storyId, storyImage, creationDate)
        }
    }
}
