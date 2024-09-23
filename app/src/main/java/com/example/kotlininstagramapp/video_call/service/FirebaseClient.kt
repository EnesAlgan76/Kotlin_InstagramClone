package com.example.turkiyefinansappclone.video_call.service

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.enesalgan.nswebrtc.models.NSDataModel
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.turkiyefinansappclone.video_call.utils.UserStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val dbRef:FirebaseFirestore,
    private val gson:Gson
) {

    private var listenerRegistration: ListenerRegistration? = null

    /*private fun saveLoginStateToSharedPreferences(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun getLoginStateToSharedPreferences(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }*/

/*    suspend fun login(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        try {
            val userDoc = dbRef.collection("users").document(username).get().await()
            if (userDoc.exists()) {
                val dbPassword = userDoc.getString("password")
                if (password == dbPassword) {
                    dbRef.collection("users").document(username).update("status", UserStatus.ONLINE).await()
                    callback(true, null)
                  //  saveLoginStateToSharedPreferences(username)
                } else {
                    callback(false, "Password is wrong")
                }
            } else {
                callback(false, "User does not exist")
            }
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }*/
/*


    suspend fun register(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        try {
            dbRef.collection("users").document(username).set(
                hashMapOf(
                    "password" to password,
                    "status" to UserStatus.ONLINE
                )
            ).await()
            //setUsername(username)
            callback(true, null)
            //LoginState.updateLoginStatus(true, username)
            //saveLoginStateToSharedPreferences(username)
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }
*/


  /*  fun observeConversations(): LiveData<List<Conversation>> {
        val liveData = MutableLiveData<List<Conversation>>()
        dbRef.collection("conversations")
           .whereArrayContains("users", LoginState.currentUserName.value.toString())
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val conversations = snapshots?.documents?.map { doc ->
                    Conversation(
                        id = doc.id,
                        users = doc.get("users") as? List<String> ?: emptyList(),
                        lastMessage = doc.getString("lastMessage").orEmpty(),
                        lastMessageTime = doc.getString("lastMessageTime").orEmpty()
                    )
                } ?: emptyList()

                val sortedConversations = conversations.sortedByDescending { it.lastMessageTime }
                liveData.value = sortedConversations

            }

        return liveData
    }
*/

    /*fun observeUsersStatus(): LiveData<Map<String, String>> {
        val liveData = MutableLiveData<Map<String, String>>()
        dbRef.collection("users")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val statuses = snapshots?.documents?.associate { doc ->
                    doc.id to doc.getString("status").orEmpty()
                } ?: emptyMap()

                liveData.value = statuses
            }

        return liveData
    }*/




    /*fun observeUsersStatus(): LiveData<List<Pair<String, String>>> {
        val liveData = MutableLiveData<List<Pair<String, String>>>()
        dbRef.collection("users")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val list = snapshots?.documents
                    ?.filter { it.id != LoginState.currentUserName.value }
                    ?.map { it.id to it.getString("status").orEmpty() }
                    ?: emptyList()

                liveData.value = list
            }

        return liveData
    }*/

    fun subscribeForLatestEvent(onLatestEventReceived: (NSDataModel, String) -> Unit) {
        listenerRegistration = dbRef.collection("notifications")
            .document(UserSingleton.userModel!!.userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }

                snapshot?.getString("latest_event")?.let { eventString ->
                    val event = try {
                        gson.fromJson(eventString, NSDataModel::class.java)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

                    event?.let {
                        val callerName = snapshot.getString("callerName")
                        onLatestEventReceived(it,callerName?:"")
                    }
                }
            }
    }

    fun unsubscribeFromLatestEvent() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    fun sendMessageToOtherClient(message: NSDataModel, success: (Boolean) -> Unit) {
        val convertedMessage = gson.toJson(message.copy(sender = UserSingleton.userModel!!.userId))
        dbRef.collection("notifications")
            .document(message.target)
            .update("latest_event", convertedMessage ,"callerName", UserSingleton.userModel!!.fullName)
            .addOnCompleteListener {
                success(true)
            }
            .addOnFailureListener {
                success(false)
            }
    }

    fun changeMyStatus(status: UserStatus) {
        dbRef.collection("notifications")
            .document(UserSingleton.userModel!!.userId)
            .update("status", status.name)
    }

    fun clearLatestEvent() {
        dbRef.collection("notifications")
            .document(UserSingleton.userModel!!.userId)
            .update("latest_event", null)
    }
}