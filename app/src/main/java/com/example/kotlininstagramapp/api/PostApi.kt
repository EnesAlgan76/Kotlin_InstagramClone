package com.example.kotlininstagramapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApi {

    @GET("posts/allposts/{userId}")
    fun getAllPosts(@Path("userId") userId : String): Call<BaseResponse>
}