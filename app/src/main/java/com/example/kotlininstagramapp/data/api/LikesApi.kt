package com.example.kotlininstagramapp.data.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LikesApi {
    @POST("likes/add")
    fun likePost(
        @Query("userId") userId: String,
        @Query("postId") postId: Int
    ): Call<BaseResponse>


    @DELETE("likes/remove")
    fun unlikePost(
        @Query("userId") userId: String,
        @Query("postId") postId: Int
    ): Call<BaseResponse>



    @GET("likes/checkLikeStatus")
    fun checkLikeStatus(
        @Query("userId") userId: String,
        @Query("postId") postId: Int
    ): Call<BaseResponse>


}