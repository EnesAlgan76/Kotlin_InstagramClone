package com.example.kotlininstagramapp.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
        val body = message.notification?.body
        val data = message.data

        println("Gelen Bildirim --------- >>> title :$title, body : $body, data : $data")

        // Check if title and body are not null before showing the notification
        if (title != null ) {
            showNotification(title, "bodyyy")
        }
    }

    override fun onNewToken(token: String) {
        FirebaseHelper().saveNewToken(token)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "GS.7673mobile" // Provide a unique channel ID
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if the device is running Android Oreo or higher, and create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Your Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon_redpoint) // Set your custom notification icon
            .setAutoCancel(true) // Close the notification when clicked

        // Show the notification
        notificationManager.notify(/* unique notification ID */ 1, notificationBuilder.build())
    }
}