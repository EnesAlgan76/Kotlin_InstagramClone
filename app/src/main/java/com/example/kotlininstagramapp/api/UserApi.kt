package com.example.kotlininstagramapp.api
import com.example.kotlininstagramapp.Models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
     @GET("users/user/{userId}")
     fun getUserById(@Path("userId") userId: Int): Call<UserModel>

     @POST("users")
     fun createUser(@Body newUser: UserModel): Call<UserModel>


     @GET("users/checkUserExists")
     fun findUserByUserNameOrEmail(
          @Query("userName") userName: String,
          @Query("email") email: String
     ): Call<Boolean>
}