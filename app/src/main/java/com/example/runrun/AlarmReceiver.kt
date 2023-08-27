package com.example.runrun

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    private val channelId = "time_related_notification_channel"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Received alarm broadcast")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if running on Android Oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Time Related Notifications"
            val descriptionText = "Channel for time related notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)  // Replace with your own drawable icon
            .setContentTitle("Time Related Notification")
            .setContentText("This is your scheduled notification.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(3, notification)
        Log.d("AlarmReceiver", "Notification shown")
    }
}
