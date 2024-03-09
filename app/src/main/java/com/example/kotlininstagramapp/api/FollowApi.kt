package com.example.kotlininstagramapp.api

import retrofit2.Call
import retrofit2.http.POST
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


    @POST("follow/checkFollowStatus")
    fun checkFollowStatus(
        @Query("followerId") followerId: String,
        @Query("followedId") followedId: String
    ) : Call<BaseResponse>


}