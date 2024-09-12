package com.example.kotlininstagramapp.data.repository

import android.util.Log
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val userService: UserApi)  {

    private val auth = FirebaseAuth.getInstance()

    suspend fun authenticateUser(email: String, password: String): UserModel? {
        return try {
            val response = userService.authenticateUser(email, password).await()
            response.data?.let { userData ->
                val userDataJson = Gson().toJson(response.data)
                val userModel = Gson().fromJson(userDataJson, UserModel::class.java)
                userModel
            }
        } catch (e: Exception) {
            null
        }
    }


    suspend fun loginUserWithFirebase(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user // Return the FirebaseUser if login is successful
        } catch (e: Exception) {
            Log.e("Firebase Auth", "Failed to login with Firebase: ${e.message}")
            null
        }
    }

    fun deleteUserFromFirebase(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase Auth", "User deleted from Firebase Auth successfully.")
            } else {
                Log.e("Firebase Auth", "Failed to delete user from Firebase: ${task.exception?.message}")
            }
        }
    }


    suspend fun checkUserExists(userName: String, email: String, phoneNumber: String) :Boolean{
        val isExists = userService.checkUserExists(userName, email, phoneNumber).await()
        return isExists

    }

    fun registerUser(userName: String,fullName:String, email: String, phoneNumber: String, password: String) {
        var fakeMail =email
        if (email.isEmpty()){fakeMail = phoneNumber+"@enes.com"}

        println(fakeMail+ password)

        auth.createUserWithEmailAndPassword(fakeMail,password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    val userID = auth.currentUser!!.uid
                    val userModel = UserModel(
                        userId =userID ,
                        userName = userName,
                        password = password,
                        phoneNumber = phoneNumber,
                        email = fakeMail,
                        fullName = fullName,
                        fcmToken = "",
                        profilePicture = "",
                        biography = "",
                        followerCount = 0,
                        postCount = 0,
                        followingCount = 0
                    )


                    CoroutineScope(Dispatchers.IO).launch {
                        val createCall = userService.createUser(userModel)
                        val createResponse = createCall.execute()
                        if (createResponse.isSuccessful) {
                            println("Hesap başarıyla oluşturuldu.")
                            println("User create body : ${createResponse.body()}")
                        } else {
                            println("Hata Mesajı : ${createResponse.errorBody()?.string()}")
                        }
                        auth.signOut()
                    }



                }
            }


    }

    suspend fun deleteUserFromDatabase(userId: String) {
        try {
            val response = userService.deleteUserById(userId).await() // Delete user by userId
            if (response.status) {
                Log.d("UserRepository", "User deleted successfully from database.")
            } else {
                Log.e("UserRepository", "Failed to delete user from database: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user from database: ${e.message}")
        }
    }


}