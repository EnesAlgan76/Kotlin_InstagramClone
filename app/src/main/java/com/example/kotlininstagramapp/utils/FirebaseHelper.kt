package com.example.kotlininstagramapp.Profile

import Comment
import android.net.Uri
import android.util.Log
import com.example.kotlininstagramapp.Home.CommentsAdapter
import com.example.kotlininstagramapp.Models.Post
import com.example.kotlininstagramapp.Models.User
import com.example.kotlininstagramapp.Models.UserPostItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FirebaseHelper {
    private val storageReference = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
    val commentCollection =db.collection("comments")


    suspend fun getAllPosts(): ArrayList<UserPostItem> {
        val userIdList = mutableListOf<String>()
        val allPostsList = arrayListOf<UserPostItem>()

        val followedUsers = userDocumentRef.collection("follows").get().await()
        for (doc in followedUsers.documents) {
            userIdList.add(doc.id)
        }

        val deferredList = userIdList.map { userId ->
            CoroutineScope(Dispatchers.IO).async {
                fetchUserPosts(userId)
            }
        }

        val results = deferredList.awaitAll()
        results.forEach { allPostsList.addAll(it) }

        return allPostsList
    }



    suspend fun fetchUserPosts(userId: String): ArrayList<UserPostItem> {
        val list: ArrayList<UserPostItem> = arrayListOf()

        val userDocument = db.collection("users").document(userId)
        val document = userDocument.get().await()

        if (document.exists()) {
            val user: User = User.fromMap(document.data as Map<String, Any>)
            val postDocumentReference = db.collection("userPosts").document(user.userId)
            val allPostsCollection = postDocumentReference.collection("posts")

            val postDocuments = allPostsCollection.get().await()
            for (i in postDocuments) {
                val dataMap = i.data as MutableMap<String, String>
                var likeCount: String = dataMap["likeCount"] ?: "null"

                dataMap["userId"] = user.userId
                dataMap["postId"] = i.id
                val post: Post = Post.fromMap(dataMap)
                val userPostItem = UserPostItem(
                    post.postId,
                    user.userId,
                    post.explanation,
                    user.userName,
                    post.url,
                    post.date,
                    user.userDetails.profilePicture,
                    likeCount
                )

                list.add(userPostItem)
            }
        }

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
        val userDocument = userDocumentRef.get().await()
        val likedComments = userDocument.get("liked_comments") as? List<String>?: listOf()


        val commentsList = comments.documents.mapNotNull{ document ->
            val comment = document.toObject(Comment::class.java)
            val isLiked=  likedComments.contains(comment!!.commentId)
            (comment to isLiked)

        }

        return ArrayList(commentsList)

    }


    suspend fun updateCommentLikeState(commentId: String, currentLikeCount: Int) {
        var liked :Boolean? =null
        try {
            val documentSnapshot = userDocumentRef.get().await()

            val likedComments = documentSnapshot.get("liked_comments") as? List<String> ?: listOf()   // Kullanıcının daha önceden yorumu beğenip
            liked = likedComments.contains(commentId) // beğenmediğini kontrol et
            Log.e("Kullancı önceden Yourumu beğenmiş mi : ", liked.toString())
            Log.e("İlk Beğeni Sayısı : ", currentLikeCount.toString())
            val newLikeCount = if (liked) currentLikeCount - 1 else currentLikeCount + 1
            Log.e("yeni sayı : ", newLikeCount.toString())// beğenmediğini kontrol et



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



    suspend fun updateLikeCount(postId: String, userId: String, increase: Boolean) {
        val postDocumentReference = db.collection("userPosts").document(userId).collection("posts")
        val document= postDocumentReference.document(postId).get().await()
        val currentlikeCount : String = (document["likeCount"]?:"0").toString()
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

    suspend fun followUser(userId: String) {
        val currentuser = firebaseAuth.currentUser
        if(currentuser != null){
            userDocumentRef.collection("follows").document(userId).set((mapOf("userId" to userId))).await()
            db.collection("users").document(userId).collection("followers").document(currentuser.uid).set(mapOf("userId" to currentuser.uid)).await()
            Log.e("////","Takip Edildi")
        }
    }

    suspend fun unfollowUser(userId: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            userDocumentRef.collection("follows").document(userId).delete().await()
            db.collection("users").document(userId).collection("followers").document(currentUser.uid).delete().await()
            Log.e("////", "Unfollowed")
        }
    }

    fun isUserFollowing(userId :String, callback :(Boolean) ->Unit){
        val documentRef = userDocumentRef.collection("follows").document(userId).get().addOnSuccessListener {
                snapshot ->
            if(snapshot.exists()){
                callback(true)
            }else{
                callback(false)
            }
        }
    }

}
