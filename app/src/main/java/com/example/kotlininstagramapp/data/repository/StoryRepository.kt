package com.example.kotlininstagramapp.data.repository

import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.data.api.BaseResponse
import com.example.kotlininstagramapp.data.api.StoryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject


class StoryRepository @Inject constructor(private val storyService: StoryApi) {

    suspend fun addStory(userId: String, storyImage: String, creationDate: Long): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = storyService.addStory(userId, storyImage, creationDate).await()
                response.message
            } catch (e: Exception) {
                "Failed to add story: ${e.message}"
            }
        }
    }

    suspend fun getUserStories(userId: String): Story? {
        return withContext(Dispatchers.IO) {
            try {
                val response = storyService.getUserStories(userId).await()
                response.data as? Story
            } catch (e: Exception) {
                println("Failed to retrieve stories: ${e.message}")
                null
            }
        }
    }
}