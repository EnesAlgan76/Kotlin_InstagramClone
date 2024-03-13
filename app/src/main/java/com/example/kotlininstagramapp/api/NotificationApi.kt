package com.example.kotlininstagramapp.api

import com.example.kotlininstagramapp.api.model.NotificationModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {

    @POST("notifications")
    fun addNotification(@Body notificationService: NotificationModel) : Call<BaseResponse>

    @GET("notifications/getAllUserNotifications")
    fun getAllUserNotifications(@Query("userId") userId : String): Call<BaseResponse>


    @DELETE("notifications/delete")
    fun deleteNotification(@Query("notificationId") notificationId: Int): Call<BaseResponse>

}