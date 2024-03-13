package com.example.kotlininstagramapp.utils

import android.net.Uri
import android.util.Log
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.Models.Notification
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.api.BaseResponse
import com.example.kotlininstagramapp.api.FollowApi
import com.example.kotlininstagramapp.api.NotificationApi
import com.example.kotlininstagramapp.api.PostApi
import com.example.kotlininstagramapp.api.RetrofitInstance
import com.example.kotlininstagramapp.api.UserApi
import com.example.kotlininstagramapp.api.model.NotificationModel
import com.example.kotlininstagramapp.api.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.await
import java.util.UUID

class DatabaseHelper {

    val userService = RetrofitInstance.retrofit.create(UserApi::class.java)
    val postService = RetrofitInstance.retrofit.create(PostApi::class.java)
    val followService = RetrofitInstance.retrofit.create(FollowApi::class.java)
    val notificationService = RetrofitInstance.retrofit.create(NotificationApi::class.java)
    suspend fun getUserById(userId: String): UserModel? {
        var userData: Map<String, Any>? = null
        try {
            val response = userService.getUserById(userId).await()
            userData = response.data as Map<String, Any>
        } catch (e: Exception) {
            println(e.toString())
        }
        userData?.let {
            return UserModel.fromMap(userData)
        } ?: return null
    }

    suspend fun uploadPost(
        compressedMediaUri: Uri,
        image: Boolean,
        explanation: String,
        onSuccess: (message :String) -> Unit,
        onProgress: (progress: Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit
    ) {
        val currentUserId = UserSingleton.userModel?.userId
        val postId = UUID.randomUUID()
        val storageReference = FirebaseStorage.getInstance().reference

        val imageRef = storageReference.child("posts/${currentUserId}/images/${postId}")
        val videoRef = storageReference.child("posts/${currentUserId}/videos/${postId}")


        try {
            val mediaRef: StorageReference = if (image) {
                imageRef
            } else {
                videoRef
            }

            val uploadTask = mediaRef.putFile(compressedMediaUri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                onProgress(progress)
            }

            uploadTask.addOnSuccessListener { taskSnapshot ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val url = mediaRef.downloadUrl.await().toString()
                        val post = Post(
                            currentUserId!!,
                            0.1,
                            System.currentTimeMillis().toString(),
                            explanation,
                            url
                        )

                        val result= postService.createPost(post.toMap()).await()
                        if (result.status){
                            val incrementResponse = userService.incrementPostCount(currentUserId).await()
                            if (incrementResponse.status){
                                onSuccess(result.message+ ", "+ incrementResponse.message)
                            }else{
                                onFailed("backend error: "+incrementResponse.message)
                            }
                        }else{
                            onFailed("backend error: "+result.message)
                        }

                    } catch (e: Exception) {
                        onFailed(e.message ?: "Unknown error occurred")
                    }
                }
            }
        } catch (e: Exception) {
            onFailed(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun isUserFollowing(userId: String): Boolean {
        val response :BaseResponse = followService.checkFollowStatus(UserSingleton.userModel!!.userId,userId).await()
        if (response.status){
            return response.data as Boolean
        }else{
            Log.e("isUserFollowing", response.message)
            return false
        }
    }

    suspend fun sendFollowRequest(userId: String) {
        val fcmToken = getFCMToken(userId)
        if(fcmToken!=null){

            // Those are for trigger firebase messaging by firebase functions
            val db = FirebaseFirestore.getInstance()
            val newNotificationDoc =  db.collection("notifications").document()
            val currentTimestamp = System.currentTimeMillis()
            val currentUser = UserSingleton.userModel
            val notification = mapOf(
                "fcmToken" to fcmToken,
                "userName" to currentUser!!.userName,
                "type" to "follow_request",
                "timestamp" to currentTimestamp
            )
            newNotificationDoc.set(notification).await()


            // This for save to main database to show notification in notification page

            val notificationModel = NotificationModel(
                    1.0,
                    userId,
                "follow_request",
                currentTimestamp.toString(),
                "null",
                currentUser.userId,
                currentUser.profilePicture,
                currentUser.userName
            )

            val response = notificationService.addNotification(notificationModel).await()
            if (response.status){
                Log.e("sendFollowRequest SUCCESS", response.message)
            }else{
                Log.e("sendFollowRequest FAIL", response.message)
            }

        }else{
            Log.e("FAIL","FCM Token is NULL")
        }

    }




    suspend fun getFCMToken(userId: String): String? {
        val response = userService.getFCMToken(userId).await()
        if (response.status){
            return response.data as String
        }else{
            return null;
        }
    }

    suspend fun getNotifications() :List<NotificationModel> {
        val response  = notificationService.getAllUserNotifications(UserSingleton.userModel!!.userId).await()
        if (response.status){
            val notificationList =  response.data as List<Map<String, String>>
            Log.e("Spring getAllUserNotifications: ", response.message)
            return notificationList.map {NotificationModel.fromMap(it)}
        }else{
            Log.e("Spring getAllUserNotifications: ", response.message)
            return listOf()
        }
    }

    suspend fun acceptFollowRequest(follower: String, notificationId: Double) {
        val response = followService.followUser(follower,UserSingleton.userModel!!.userId).await()
        if(response.status){
            Log.e("SPRING followUser: ",response.message)
            try {
                deleteNotification(notificationId.toInt())
            } catch (e: Exception) {
                Log.e("SPRING deleteNotification ERROR: ",e.toString())
            }

            try {
                incrementFollowCount(follower)
            } catch (e: Exception) {
                Log.e("SPRING incrementFollowCount ERROR: ",e.toString())
            }

            try {
                incrementFollowerCount(UserSingleton.userModel!!.userId)
            } catch (e: Exception) {
                Log.e("SPRING incrementFollowerCount ERROR: ",e.toString())
            }

        }else Log.e("SPRING followUser: ",response.message)
    }

    suspend fun incrementFollowerCount(userId: String) {
        val response = userService.incrementFollowerCount(userId).await()
        Log.e("SPRING incrementFollowerCount", response.message)
    }

    suspend fun incrementFollowCount(userId: String) {
        val response = userService.incrementFollowCount(userId).await()
        Log.e("SPRING incrementFollowCount", response.message)
    }

    suspend fun deleteNotification(notificationId: Int) {
        val response = notificationService.deleteNotification(notificationId).await()
        Log.e("SPRING deleteNotification", response.message)
    }

}