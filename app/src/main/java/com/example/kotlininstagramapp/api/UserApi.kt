package com.example.kotlininstagramapp.api
import com.example.kotlininstagramapp.api.model.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
     @GET("users/user/{userId}")
     fun getUserById(@Path("userId") userId: String): Call<BaseResponse>

     @POST("users")
     fun createUser(@Body newUser: UserModel): Call<UserModel>


     @GET("users/checkUserExists")
     fun checkUserExists(
          @Query("userName") userName: String,
          @Query("email") email: String,
          @Query("phoneNumber") tel: String,
     ): Call<Boolean>

     @GET("users/authenticateUser")
     fun authenticateUser(
          @Query("userNameOrTelOrMail") userNameOrTelOrMail: String,
          @Query("password") password: String,
     ): Call<BaseResponse>


     @PUT("users/updateFcmToken")
     fun updateFcmToken(
          @Query("userId") userId: String,
          @Query("newFcmToken") newFcmToken: String
     ): Call<BaseResponse>


     @GET("users/searchUsersByUsername/{searchText}")
     fun searchUsersByUsername(@Path(value = "searchText") searchText: String) : Call<BaseResponse>


}