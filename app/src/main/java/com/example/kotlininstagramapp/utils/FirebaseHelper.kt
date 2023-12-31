package com.example.kotlininstagramapp.Profile

import Comment
import android.net.Uri
import android.util.Log
import com.example.kotlininstagramapp.Home.CommentsAdapter
import com.example.kotlininstagramapp.Models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FirebaseHelper {
    private val storageReference = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    val currentUserDocumentRef = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
    val commentCollection =db.collection("comments")


    suspend fun getAllPosts(): ArrayList<UserPostItem> {
        val userIdList = mutableListOf<String>()
        val allPostsList = arrayListOf<UserPostItem>()

        val followedUsers = currentUserDocumentRef.collection("follows").get().await()
        for (doc in followedUsers.documents) {
            userIdList.add(doc.id)
        }
        val deferredList = userIdList.map { userId ->
            CoroutineScope(Dispatchers.IO).async {
                val user = getUserById(userId)
                if (user != null) {
                    fetchUserPosts(user)
                }else{
                    emptyList()
                }
            }
        }
        val results = deferredList.awaitAll()
        results.forEach { allPostsList.addAll(it) }
        return allPostsList
    }


    suspend fun fetchUserPosts(user: User): ArrayList<UserPostItem> {
        val userPosts: ArrayList<UserPostItem> = arrayListOf()

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
                user.userFullName,
                post.url,
                post.date,
                user.userDetails.profilePicture,
                likeCount
            )

            userPosts.add(userPostItem)
        }

        return userPosts
    }



     suspend fun getUserById(userId: String): User? {
        val userDocument = db.collection("users").document(userId).get().await()
        return if (userDocument.exists()) {
            User.fromMap(userDocument.data as Map<String, Any>)
        } else {
            null
        }
    }




    suspend fun updateUserProfile(fullName: String?, userName: String?, biography: String?, selectedImageUri: Uri?, ){
        if (fullName != null) {
            currentUserDocumentRef.update("userFullName", fullName).await()
            println("Tam Ad Güncellendi")
        }

        if (biography != null) {
            currentUserDocumentRef.update(FieldPath.of("userDetails", "biography"), biography).await()
            println("Biyogafi Güncellendi")
        }

        if (userName != null) {
            val querySnapshot = db.collection("users").whereEqualTo("userName", userName).get().await()
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                println("Kullanıcı Adı Zaten Var")
            } else {
                currentUserDocumentRef.update("userName", userName).await()
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
                currentUserDocumentRef.update(FieldPath.of("userDetails", "profilePicture"), url).await()
                println("Profile Picture Updated")
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    suspend fun publishComment(text: String, postId: String, adapter: CommentsAdapter) {
        if(firebaseAuth.currentUser!=null){
           val userDoc = currentUserDocumentRef.get().await()
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
        val userDocument = currentUserDocumentRef.get().await()
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
            val documentSnapshot = currentUserDocumentRef.get().await()

            val likedComments = documentSnapshot.get("liked_comments") as? List<String> ?: listOf()   // Kullanıcının daha önceden yorumu beğenip
            liked = likedComments.contains(commentId) // beğenmediğini kontrol et
            val newLikeCount = if (liked) currentLikeCount - 1 else currentLikeCount + 1



            val commentRef = db.collection("comments").document(commentId)   // Beğeni Sayısını Güncelle
            commentRef.update("like_count", newLikeCount.toString()).await()                   //


            val updateTask = if (!liked) {
                currentUserDocumentRef.update("liked_comments", FieldValue.arrayUnion(commentId))  // duruma göre kullanıcı füğümünden de likedComments güncelle
            } else {
                currentUserDocumentRef.update("liked_comments", FieldValue.arrayRemove(commentId))
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
            println("------------------ "+ errorMessage)
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
        val likedPostDocRef = currentUserDocumentRef.collection("liked_posts").document(postId)
        return try {
            val documentSnapshot = likedPostDocRef.get().await()
            documentSnapshot.exists()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getFollowedFromUser(userId: String){
        val currentuser = firebaseAuth.currentUser
        val userDocumentRef = db.collection("users").document(userId)
        if(currentuser != null){
            userDocumentRef.collection("follows").document(currentuser.uid).set((mapOf("userId" to currentuser.uid))).await()
            currentUserDocumentRef.collection("followers").document(userId).set(mapOf("userId" to userId)).await()
            Log.e("////","Takip Edildi")
        }

    }

    suspend fun followUser(userId: String) {
        val currentuser = firebaseAuth.currentUser
        if(currentuser != null){
            currentUserDocumentRef.collection("follows").document(userId).set((mapOf("userId" to userId))).await()
            db.collection("users").document(userId).collection("followers").document(currentuser.uid).set(mapOf("userId" to currentuser.uid)).await()
            Log.e("////","Takip Edildi")
        }
    }

    suspend fun getNotifications():List<Notification> {
        var notificationList = mutableListOf<Notification>()
        val snapshot= db.collection("users").document(firebaseAuth.currentUser!!.uid).collection("notifications").get().await()
        snapshot.documents.forEach { documentSnapshot ->
            val notification :Notification = Notification.fromMap(documentSnapshot.data as Map<String, Any>)
            notificationList.add(notification)
        }
        return notificationList
    }

    suspend fun sendFollowRequest(userId: String) {
      val newNotificationDoc =  db.collection("users").document(userId).collection("notifications").document()
      val currentTimestamp = FieldValue.serverTimestamp()
      val currentUser = getUserById(firebaseAuth.currentUser!!.uid)
      val notification = mapOf(
            "id" to newNotificationDoc.id,
            "user_id" to currentUser!!.userId,
            "profile_image" to currentUser.userDetails.profilePicture,
            "user_name" to currentUser.userName,
            "type" to "follow_request",
            "timestamp" to currentTimestamp,
            "post_preview" to "null"
        )
      newNotificationDoc.set(notification)
    }

    fun deleteFollowRequestNotification(id: String) {
        val notificationDoc =  db.collection("users").document(firebaseAuth.currentUser!!.uid).collection("notifications").document(id)
        notificationDoc.delete().addOnCompleteListener { task ->
            if(task.isSuccessful){
                println("******** >> Bildirim silindi")
            }
        }
    }

    suspend fun sendLikeNotification(userId: String, postUrl : String){
        val newNotificationDoc =  db.collection("users").document(userId).collection("notifications").document()
        val currentTimestamp = FieldValue.serverTimestamp()
        val currentUser = getUserById(firebaseAuth.currentUser!!.uid)
        val notification = mapOf(
            "id" to newNotificationDoc.id,
            "user_id" to currentUser!!.userId,
            "profile_image" to currentUser!!.userDetails.profilePicture,
            "user_name" to currentUser.userName,
            "type" to "post_like",
            "timestamp" to currentTimestamp,
            "post_preview" to postUrl
        )
        newNotificationDoc.set(notification)
    }

    suspend fun sendCommentNotification(userId: String, postUrl: String, comment : String){
        val newNotificationDoc =  db.collection("users").document(userId).collection("notifications").document()
        val currentTimestamp = FieldValue.serverTimestamp()
        val currentUser = getUserById(firebaseAuth.currentUser!!.uid)
        val notification = mapOf(
            "id" to newNotificationDoc.id,
            "user_id" to currentUser!!.userId,
            "profile_image" to currentUser!!.userDetails.profilePicture,
            "user_name" to currentUser.userName,
            "type" to "comment",
            "timestamp" to currentTimestamp,
            "post_preview" to postUrl
        )
        newNotificationDoc.set(notification)
    }

    suspend fun unfollowUser(userId: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            currentUserDocumentRef.collection("follows").document(userId).delete().await()
            db.collection("users").document(userId).collection("followers").document(currentUser.uid).delete().await()
            Log.e("////", "Unfollowed")
        }
    }

    suspend fun isUserFollowing(userId :String): Boolean {
        val snapshot = currentUserDocumentRef.collection("follows").document(userId).get().await()
        return snapshot.exists()
    }


    fun getConversations(
        onConversationAddedToList: (conversation: Conversation) -> Unit,
        onNewMessageReceivedUpdateConversation: (index :Int, conversation: Conversation) -> Unit,
        onConversationClickedResetColor: (index :Int, conversation: Conversation) -> Unit,
        onUpdateLastMessageInConversation: (index :Int, conversation: Conversation) -> Unit,
    ) {
        val conversationList: ArrayList<Conversation> = arrayListOf()
        val conversationsCollectionRef = currentUserDocumentRef.collection("conversations")

        conversationsCollectionRef.orderBy("last_view", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen conversation failed: $error")
                    return@addSnapshotListener
                }


                for (doc in value!!.documentChanges) {
                    val conversation = Conversation.fromMap(doc.document.data as Map<String, Any>)
                    conversation.conversation_id = doc.document.id

                    when (doc.type) {
                        DocumentChange.Type.ADDED -> {
                            println("--------->>> ADDED")
                            conversationList.add(conversation)
                            onConversationAddedToList(conversation)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            println("--------->>> MODIFIED")
                            val index = conversationList.indexOfFirst { it.conversation_id == conversation.conversation_id }
                            val oldConv = conversationList.get(index)
                            val newConv = conversation

                            if (oldConv.isRead == true && newConv.isRead == false){
                                onNewMessageReceivedUpdateConversation(index, conversation)
                                conversationList.removeAt(index)
                                if(conversationList.isEmpty()){
                                    conversationList.add(conversation)
                                }else{
                                    conversationList[0] = conversation // it gives error if list is empty
                                }

                                println("----------->> Yeni mesaj geldi yukarı taşı")

                            }else if (oldConv.isRead == false && newConv.isRead == true){
                                onConversationClickedResetColor(index, conversation)
                                conversationList[index] = conversation
                                println("----------->> üzerine tıklantı sadece rengi eski haline çevir.")

                            }else{
                                println("----------->> sadece yeni mesaj. konuşmaya su an koyu renkte : ${conversation}")
                                conversationList[index] = conversation
                                onUpdateLastMessageInConversation(index, conversation)
                            }

                        }
                        else -> {
                        }
                    }
                }


            }
    }



    suspend fun createNewConversation(userId: String, userName: String, profileImage: String, userFullName: String, message: String): String {
        val conversationRef = db.collection("conversations")
        val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser!!.uid)

        val currentUserConversations = userDocumentRef.collection("conversations")
            .whereEqualTo("user_id", userId)
            .get()
            .await()

        return if (currentUserConversations.isEmpty) {
            Log.e("//","Conversation does not exist, create a new conversation")
            val newConversationDocument = conversationRef.document()
            val firstMessage = mapOf<String, Any>(
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp(),
                "sender_id" to firebaseAuth.currentUser!!.uid,
                "receiver_id" to userId
            )
            newConversationDocument.collection("messages").add(firstMessage).await()

            val newConversation = mapOf<String, Any>(
                "last_message" to message,
                "last_view" to FieldValue.serverTimestamp(),
              //  "profile_image" to profileImage,
              //  "user_full_name" to userFullName,
                "user_id" to userId,
                "is_read" to true,
               // "user_name" to userName
            )
            userDocumentRef.collection("conversations").document(newConversationDocument.id).set(newConversation).await()

            val currentUserObject = getUserById(firebaseAuth.currentUser!!.uid)

            val newConversationForOtherUser = mapOf<String, Any>(
                "last_message" to message,
                "last_view" to FieldValue.serverTimestamp(),
               // "profile_image" to currentUserObject!!.userDetails.profilePicture,
             //   "user_full_name" to currentUserObject.userFullName,
                "user_id" to currentUserObject!!.userId,
                "is_read" to false,
              //  "user_name" to currentUserObject.userName
            )

            val otherUserDocumentRef = db.collection("users").document(userId)
            otherUserDocumentRef.collection("conversations").document(newConversationDocument.id).set(newConversationForOtherUser).await()

            newConversationDocument.id // Return the newly created conversation ID
        } else {
            Log.e("//","Conversation already exists, only send the message")
            val conversationId = currentUserConversations.documents[0].id
            val messageData = mapOf<String, Any>(
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp(),
                "sender_id" to firebaseAuth.currentUser!!.uid,
                "receiver_id" to userId
            )
            conversationRef.document(conversationId).collection("messages").add(messageData).await()

            conversationId // Return the existing conversation ID
        }
    }


    fun sendMessage(
        message: String,
        to: String,
        conversation_id: String,
        firstMessageSended: Boolean
    ) {
        val receiverConversationDoc = db.collection("users").document(to).collection("conversations").document(conversation_id)
        receiverConversationDoc.update("is_read",false)
        receiverConversationDoc.update("last_message",message)

        if (!firstMessageSended){
            receiverConversationDoc.update("last_view",FieldValue.serverTimestamp())
        }

        val messagesRef = db.collection("conversations").document(conversation_id).collection("messages")

        val messageObject = hashMapOf(
            "message" to message,
            "receiver_id" to to,
            "sender_id" to firebaseAuth.currentUser!!.uid,
            "timestamp" to FieldValue.serverTimestamp()
        )

        messagesRef.add(messageObject)
    }

    fun getMessages(conversationId: String, onMessagesLoaded: (List<ChatMessage>) -> Unit, onError: (Exception) -> Unit) {
        val messagesRef = db.collection("conversations").document(conversationId)
            .collection("messages").
            orderBy("timestamp", Query.Direction.DESCENDING)

        messagesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                onError(exception)
                return@addSnapshotListener
            }

            val messagesList = mutableListOf<ChatMessage>()

            snapshot?.documents?.forEach { document ->
                val message= ChatMessage.fromMap(document.data as Map<String, Any>)
                messagesList.add(message)
            }
            onMessagesLoaded(messagesList)
        }
    }

    fun updateConversationReadState(isRead: Boolean, conversationId: String) {
        Log.e("------------>", "Conversation State Updated ${conversationId}")
        val userDocumentRef = db.collection("users").document(firebaseAuth.currentUser!!.uid)
        val conversaionDoc = userDocumentRef.collection("conversations").document(conversationId)
        conversaionDoc.update("is_read", isRead)

    }




}
