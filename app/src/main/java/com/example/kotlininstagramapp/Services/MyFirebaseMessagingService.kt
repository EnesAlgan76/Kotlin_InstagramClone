package com.example.kotlininstagramapp.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.kotlininstagramapp.Home.NotificationsActivity
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

        if (title != null ) {
            showNotification(title, body?:title, data)
        }
    }

    override fun onNewToken(token: String) {
        FirebaseHelper().saveNewToken(token)
    }

    private fun showNotification(title: String, body: String, data: MutableMap<String, String>) {
        val channelId = "GS.7673mobile"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Your Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon_redpoint)
            .setAutoCancel(true)


        val clickAction = data["click_action"];

        if (clickAction == "NOTIFICATION_CLICK") {
            val intent = Intent(this, NotificationsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            notificationBuilder.setContentIntent(pendingIntent)
        }


        notificationManager.notify(1, notificationBuilder.build())
    }
}