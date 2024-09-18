package com.example.kotlininstagramapp.data.api
import com.example.kotlininstagramapp.data.model.UserModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
     @GET("users/user/{userId}")
     fun getUserById(@Path("userId") userId: String): Call<BaseResponse>

     @DELETE("users/user/{userId}")
     fun deleteUserById(@Path("userId") userId: String): Call<BaseResponse>

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

     @PUT("users/incrementPostCount")
     fun incrementPostCount(@Query("userId") userId: String) : Call<BaseResponse>


     @GET("users/searchUsersByUsername/{searchText}")
     fun searchUsersByUsername(@Path(value = "searchText") searchText: String) : Call<BaseResponse>


     @GET("users/getFCMToken")
     fun getFCMToken(@Query("userId") userId: String): Call<BaseResponse>


     @PUT("users/incrementFollowerCount")
     fun incrementFollowerCount(@Query("userId") userId: String) : Call<BaseResponse>


     @PUT("users/incrementFollowCount")
     fun incrementFollowCount(@Query("userId") userId: String) : Call<BaseResponse>

     @PUT("users/updateProfile")
     fun updateUserProfile(
          @Query("userName") userName: String,
          @Query("newFullName") newFullName: String?,
          @Query("newUserName") newUserName: String?,
          @Query("newBiography") newBiography: String?,
          @Query("newSelectedImageUri") newSelectedImageUri: String?
     ): Call<BaseResponse>

}