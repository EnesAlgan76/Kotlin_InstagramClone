package com.example.kotlininstagramapp.Profile

import Comment
import android.net.Uri
import android.util.Log
import com.example.kotlininstagramapp.Home.CommentsAdapter
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseHelper {
    private val storageReference = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
    val commentCollection =db.collection("comments")



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
                    val userPost = UserPost()
                    val dataMap = i.data as MutableMap<String, String>
                    var likeCount:String = dataMap.get("likeCount")?:"null"


                    dataMap["userId"] = user.userId
                    dataMap["postId"] = i.id

                    val post: Post = Post.fromMap(dataMap)

                    userPost.userName = user.userName
                    userPost.userPostUrl = post.url
                    userPost.postId = post.postId
                    userPost.postDescription = post.explanation
                    userPost.yuklenmeTarihi = post.date
                    userPost.profilePicture = user.userDetails.profilePicture
                    userPost.likeCount = likeCount

                    list.add(userPost)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions here
        }

        println("işlemler bitti")

        return list
    }


    suspend fun updateUserProfile(fullName: String?, userName: String?, biography: String?, selectedImageUri: Uri?, ){
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


    suspend fun publishComment(text: String, postId: String, adapter: CommentsAdapter) {
        if(firebaseAuth.currentUser!=null){
           val userDoc = userDocumentRef.get().await()
            val userProfilePicture = userDoc.getString("userDetails.profilePicture")?:""
            val userName= userDoc.getString("userName")?:"Unknown User"

            var comment:Comment = Comment(
                commentId = "",
                comment = text,
                like_count = "0",
                user_id = firebaseAuth.currentUser!!.uid,
                user_profile_picture = userProfilePicture,
                time = System.currentTimeMillis().toString(),
                user_name = userName,
                post_id = postId
            )

            val commentDocument = commentCollection.add(comment).await() // addOnSuccessListener
            commentDocument.update("commentId", commentDocument.id).await()

            comment.commentId =commentDocument.id
            withContext(Dispatchers.Main){
                adapter.updateComments(comment)
            }

        }

    }


    suspend fun getComments(postId: String): ArrayList<Pair<Comment,Boolean>> {
        val comments = commentCollection.whereEqualTo("post_id",postId).get().await()
        val likedComments = userDocumentRef.get().await().get("likedComments") as? List<String>?: listOf()

        val commentsList = comments.documents.mapNotNull{ document ->
            val comment = document.toObject(Comment::class.java)
            val isLiked=  likedComments.contains(comment!!.commentId)
            (comment to isLiked)
        }

        return ArrayList(commentsList)

    }


    suspend fun updateCommentLikeState(commentId: String, currentLikeCount: Int) {
//        val userId = firebaseAuth.currentUser!!.uid
//        val userLikesRef = db.collection("users").document(userId)
        var liked :Boolean? =null
        try {
            val documentSnapshot = withContext(Dispatchers.IO) {
                userDocumentRef.get().await()
            }
            val likedComments = documentSnapshot.get("liked_comments") as? List<String> ?: listOf()   // Kullanıcının daha önceden yorumu beğenip
            liked = likedComments.contains(commentId)                                                // beğenmediğini kontrol et
            val newLikeCount = if (liked) currentLikeCount - 1 else currentLikeCount + 1


            val commentRef = db.collection("comments").document(commentId)   // Beğeni Sayısını Güncelle
            commentRef.update("like_count", newLikeCount.toString()).await()                   //


            val updateTask = if (!liked) {
                userDocumentRef.update("liked_comments", FieldValue.arrayUnion(commentId))  // duruma göre kullanıcı füğümünden de likedComments güncelle
            } else {
                userDocumentRef.update("liked_comments", FieldValue.arrayRemove(commentId))
            }
            updateTask.await()

            val logMessage = if (!liked) { "Comment added to likedComments" } else { "Comment removed from likedComments" }
            Log.e("Update User Liked Comments", logMessage)

        } catch (e: Exception) {
            val errorMessage = if (liked ==null) {
                "Error fetching user: $e"
            } else if (liked){
                "Error adding comment to liked comments: $e"
            }
            else {
                "Error removing comment from liked comments: $e"
            }
            println(errorMessage)
        }
    }


    suspend fun saveUserLike(postId: String?) {
        if (postId != null) {
            val data = mapOf("post_id" to postId)
            val likedPostDocRef = userDocumentRef.collection("liked_posts").document(postId)

            try {
                val document = likedPostDocRef.get().await()

                if (document.exists()) {
                    likedPostDocRef.delete().await()
                    withContext(Dispatchers.IO){
                        updateLikeCount(postId, increase = false)
                    }

                    println("Existing document deleted for postId: $postId")
                } else {
                    likedPostDocRef.set(data).await()
                    withContext(Dispatchers.IO){
                        updateLikeCount(postId, increase = false)
                    }
                    println("Existing document added for postId: $postId")
                }
            } catch (e: Exception) {
                println("--------->> network error: ${e.message}")
            }
        }
    }


    suspend fun updateLikeCount(postId: String, increase: Boolean) {
        val postDocumentReference = db.collection("userPosts").document(firebaseAuth.currentUser!!.uid).collection("posts")
        val document= postDocumentReference.document(postId).get().await()
        val  currentlikeCount : String = (document.get("likeCount")?:"0").toString()
        val newValue = if(increase){(currentlikeCount.toInt()+1)}else{(currentlikeCount.toInt()-1)}

        postDocumentReference.document(postId).update("likeCount",newValue.toString())

        println("beğenilme sayısı güncellendi")

    }


    suspend fun isPostLiked(postId: String): Boolean {
        val likedPostDocRef = userDocumentRef.collection("liked_posts").document(postId)
        return try {
            val documentSnapshot = likedPostDocRef.get().await()
            documentSnapshot.exists()
        } catch (e: Exception) {
            throw e
        }
    }

}
