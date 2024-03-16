package com.example.kotlininstagramapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowApi {
    @POST("follow/follow")
    fun followUser(
        @Query("followerId") followerId: String,
        @Query("followedId") followedId: String
    ) : Call<BaseResponse>


    @POST("follow/follow")
    fun unfollowUser(
        @Query("followerId") followerId: String,
        @Query("followedId") followedId: String
    ) : Call<BaseResponse>


    @GET("follow/checkFollowStatus")
    fun checkFollowStatus(
        @Query("followerId") followerId: String,
        @Query("followedId") followedId: String
    ) : Call<BaseResponse>


    @GET("follow/getFollowedUserIds/{userId}")
    fun getFollowedUserIds(@Path("userId") userId: String): Call<BaseResponse>



}