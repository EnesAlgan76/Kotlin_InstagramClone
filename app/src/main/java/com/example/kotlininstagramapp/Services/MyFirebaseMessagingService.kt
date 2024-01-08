package com.example.kotlininstagramapp.Services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService :FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification!!.title
        val body = message.notification!!.body
        val data = message.data
        println("Gelen Bildirim --------- >>> title :${title}, body : ${body}, data : ${data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}