package com.example.chatapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.ui.MainActivity
import com.example.chatapp.utils.PrefUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "onNewToken: $token")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(
            "FCM",
            "onMessageReceived: ${message.notification?.body} ${message.data["userName"]} ${message.data["msgContent"]}"
        )

        val data = message.data
        val notificationTitle = message.notification?.title
        val notificationBody = message.notification?.body
        Log.d("FCM", "onMessageReceived: $notificationTitle $notificationBody")
        val userName = data["userName"]
        val messageContent = data["msgContent"]
        Log.d("FCM", "onMessageReceived: $userName $messageContent")
        if (userName != PrefUtil.getUserName() && !App.isAppInForeground)
            createNotification(userName, messageContent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(username: String?, messageContent: String?) {
        val notify = NotificationChannel(
            "message_notification",
            "New Message",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "$username: $messageContent"
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, "message_notification")
            .setSmallIcon(R.drawable.meetme)
            .setContentTitle("New Message")
            .setContentText("$username : $messageContent")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notify)
        notificationManager.notify(0, notificationBuilder.build())
    }
}