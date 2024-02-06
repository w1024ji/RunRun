package com.example.runrun

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.runrun.R


class AlarmReceiver : BroadcastReceiver() {

    private val channelId = "102"
    private val notificationId = 3 // Unique ID for the notification
    var updatedArrmsg1:String? = "example"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "onReceive()시작. Alarm broadcast 받음")

        // Start MyForegroundService
        val serviceIntent = Intent(context, MyForegroundService::class.java)
        context.startService(serviceIntent)

        var action = intent.action
        Log.d("AlarmReceiver", "action값: $action")
        when (action) {
            "UPDATE_DATA" -> {
                // 액션에 따른 동작 수행
                updatedArrmsg1 = intent.getStringExtra("arrmsg1")
                Log.d("AlarmReceiver", "Updated Message: $updatedArrmsg1")

                if (!updatedArrmsg1.isNullOrBlank()) {
                    updateNotification(context, updatedArrmsg1!!)
                }
            }
            "START_FOREGROUND_SERVICE" -> {
                updateNotification(context, updatedArrmsg1)
            }
        }
    }

    private fun updateNotification(context: Context, updatedArrmsg1: String?) {
        Log.d("AlarmReceiver", "updateNotification() 실행")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if running on Android Oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Time Related Notifications"
            val descriptionText = "Channel for time-related notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)  // Replace with your own drawable icon
            .setContentTitle("Time Related Notification")
            .setContentText("Updated Message: $updatedArrmsg1")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d("AlarmReceiver", "알림 업데이트 완료")
    }
}
