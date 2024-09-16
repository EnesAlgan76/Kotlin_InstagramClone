package com.example.kotlininstagramapp.data.repository

import android.util.Log
import com.example.kotlininstagramapp.data.api.UserApi
import com.example.kotlininstagramapp.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
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

    suspend fun registerUser(
        userName: String,
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        onError: (String?) -> Unit
    ) {
        try {
            val fakeEmail = if (email.isEmpty()) "$phoneNumber@enes.com" else email

            // Create user with email and password asynchronously
            val authResult = auth.createUserWithEmailAndPassword(fakeEmail, password).await()
            val userID = authResult.user?.uid ?: throw Exception("Failed to retrieve user ID")

            val userModel = UserModel(
                userId = userID,
                userName = userName,
                password = password,
                phoneNumber = phoneNumber,
                email = fakeEmail,
                fullName = fullName,
                fcmToken = "",
                profilePicture = "",
                biography = "",
                followerCount = 0,
                postCount = 0,
                followingCount = 0
            )

            // Perform network call to create user asynchronously
            userService.createUser(userModel).await()
            auth.signOut()
            onError(null)
        } catch (e: Exception) {
            onError("Registration failed: ${e.message}")
            Log.e("RegisterUser", e.toString()) // Add a tag for better log identification
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