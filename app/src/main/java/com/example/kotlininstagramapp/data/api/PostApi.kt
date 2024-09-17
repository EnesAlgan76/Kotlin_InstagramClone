package com.example.kotlininstagramapp.data.api

import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.Models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {

    @GET("posts/allposts/{userId}")
    fun getAllPosts(@Path("userId") userId : String): Call<BaseResponse>

    @POST("/posts")
    fun createPost(@Body post: Post) : Call<BaseResponse>

    @GET("posts/getUserPostsHomePage/{userId}")
    fun getUserPostsHomePage(@Path("userId") userId: String): Call<BaseResponse>

    @GET("posts/pagedPosts")
    fun getPagedPostsByUserId(
        @Query("userId") userId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BaseResponse>


    @GET("posts/pagedPostsFollowed")
    fun getPagedPostsFromFollowedUsers(
        @Query("userId") userId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BaseResponse>


}