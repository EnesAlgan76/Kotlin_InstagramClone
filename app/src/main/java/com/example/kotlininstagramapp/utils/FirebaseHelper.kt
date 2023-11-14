package com.example.kotlininstagramapp.Profile

import android.net.Uri
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseHelper {
    private val storageReference = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())


    fun getUserPosts2(uid: String): ArrayList<UserPost> {
        var list: ArrayList<UserPost> = arrayListOf()
        userDocumentRef.get().addOnSuccessListener { document ->
            if (document != null) {

                var user: User =User.fromMap(document.data as Map<String, Any>)

                var postDocumentReference = db.collection("userPosts").document(user.userId)
                var allPostsCollection = postDocumentReference.collection("posts")
                allPostsCollection.get().addOnSuccessListener { post_documents ->
                    if (post_documents != null) {
                        for (i in post_documents){
                            var userPost:UserPost = UserPost()

                            val dataMap = i.data as MutableMap<String, Any>
                            dataMap["userId"] =user.userId
                            dataMap["postId"] = i.id

                            var post:Post = Post.fromMap(dataMap)

                            userPost.userName=user.userName
                            userPost.userPostUrl = post.url
                            userPost.postId = post.postId
                            userPost.postDescription=post.explanation
                            userPost.yuklenmeTarihi = post.date

                            list.add(userPost)
                        }

                    }

                }

            }else{

            }

        }
        println(list.toString())
        return list
    }

    suspend fun getUserPosts(uid: String): ArrayList<UserPost> {
        val list: ArrayList<UserPost> = arrayListOf()

        try {
            val document = userDocumentRef.get().await()
            if (document.exists()) {
                val user: User = User.fromMap(document.data as Map<String, Any>)
                val postDocumentReference = db.collection("userPosts").document(user.userId)
                val allPostsCollection = postDocumentReference.collection("posts")

                val postDocuments: QuerySnapshot = allPostsCollection.get().await()
                for (i in postDocuments) {
                    val userPost: UserPost = UserPost()

                    val dataMap = i.data as MutableMap<String, Any>
                    dataMap["userId"] = user.userId
                    dataMap["postId"] = i.id

                    val post: Post = Post.fromMap(dataMap)

                    userPost.userName = user.userName
                    userPost.userPostUrl = post.url
                    userPost.postId = post.postId
                    userPost.postDescription = post.explanation
                    userPost.yuklenmeTarihi = post.date
                    userPost.profilePicture = user.userDetails.profilePicture

                    list.add(userPost)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions here
        }

        println("işlemler bitti")

        return list
    }


    suspend fun updateUserProfile(
        fullName: String?,
        userName: String?,
        biography: String?,
        selectedImageUri: Uri?,
    ){
        if (fullName != null) {
            userDocumentRef.update("userFullName", fullName).await()
            println("Tam Ad Güncellendi")
        }

        if (biography != null) {
            userDocumentRef.update(FieldPath.of("userDetails", "biography"), biography).await()
            println("Biyogafi Güncellendi")
        }

        if (userName != null) {
            val querySnapshot = db.collection("users").whereEqualTo("userName", userName).get().await()
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                println("Kullanıcı Adı Zaten Var")
            } else {
                userDocumentRef.update("userName", userName).await()
                updateProfileImage(selectedImageUri, userName)
                println("Kullanıcı Adı Güncellendi")
            }
        } else {
            updateProfileImage(selectedImageUri, userName)
        }
    }



    private suspend fun updateProfileImage(selectedImageUri: Uri?, userName: String?) {
        if (selectedImageUri != null) {
            val imageRef = storageReference.getReference("profileImages/$userName")

            try {
                val uploadTask = imageRef.putFile(selectedImageUri).await()
                val url = uploadTask.storage.downloadUrl.await().toString()
                userDocumentRef.update(FieldPath.of("userDetails", "profilePicture"), url).await()
                println("Profile Picture Updated")
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
