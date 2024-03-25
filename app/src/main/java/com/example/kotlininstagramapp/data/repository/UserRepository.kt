package com.example.kotlininstagramapp.data.repository

import android.content.Intent
import android.widget.Toast
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Home.HomeActivity
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.data.model.UserModel
import com.example.kotlininstagramapp.ui.Login.LoginCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import retrofit2.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val userService: UserApi)  {

    private val auth = FirebaseAuth.getInstance()
//    suspend fun checkUserExists(email: String, password: String): UserModel? {
//        val response = userService.checkUserExists(email, password).execute()
//        if (response.isSuccessful) {
//            println("Response Body: ${response.body()}")
//            if (response.body()?.data != null) {
//                val userDataJson = Gson().toJson(response.body()?.data)
//                val userModel = Gson().fromJson(userDataJson, UserModel::class.java)
//                UserSingleton.userModel = userModel
//                return  userModel
//            }else{
//                return null
//            }
//        } else {
//            return null
//        }
//    }

    suspend fun checkUserExists(email: String, password: String): UserModel? {
        return try {
            val response = userService.checkUserExists(email, password).await()
            println("Response Body: ${response}")
            response.data?.let { userData ->
                val userDataJson = Gson().toJson(response.data)
                val userModel = Gson().fromJson(userDataJson, UserModel::class.java)
                UserSingleton.userModel = userModel
                userModel
            }
        } catch (e: Exception) {
            null
        }
    }


    fun loginUserWithEmail(mail: String?,password: String?, callback: LoginCallback) {
        if(password!=null && mail!=null){
            println(mail)
            println(password)
            auth.signInWithEmailAndPassword(mail,password)
                .addOnSuccessListener {
                    callback.onLoginSuccess()
                }
                .addOnFailureListener { exception ->
                    println(exception.message)
                    callback.onLoginFailure(exception.message ?: "An error occurred")
                }


        }

    }

}