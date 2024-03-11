package com.example.kotlininstagramapp.api

import com.example.kotlininstagramapp.Models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApi {

    @GET("posts/allposts/{userId}")
    fun getAllPosts(@Path("userId") userId : String): Call<BaseResponse>



    @POST
    fun createPost(@Body user: Map<String,String>) : Call<BaseResponse>


}