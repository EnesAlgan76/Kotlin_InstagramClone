package com.example.kotlininstagramapp.data.api

import com.example.kotlininstagramapp.Models.Story
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryApi {
    @POST("stories/{userId}")
    fun addStory(
        @Path("userId") userId: String,
        @Query("storyImage") storyImage: String,
        @Query("creationDate") creationDate: Long
    ): Call<BaseResponse>

    @GET("stories/{userId}")
    fun getUserStories(
        @Path("userId") userId: String
    ): Call<BaseResponse>
}