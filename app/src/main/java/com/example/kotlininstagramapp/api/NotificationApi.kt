package com.example.kotlininstagramapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {

    @POST("notifications")
    fun addNotification(@Body notificationService: Map<String, String>) : Call<BaseResponse>

    @GET("notifications/getAllUserNotifications")
    fun getAllUserNotifications(@Query("userId") userId : String): Call<BaseResponse>

}