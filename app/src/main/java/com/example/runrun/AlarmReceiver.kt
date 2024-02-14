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

    private val channelId = "102"
    private val notificationId = 3 // Unique ID for the notification
    var updatedArrmsg1 : String? = "example"
    lateinit var ordId : String
    lateinit var routeId : String
    lateinit var nodeId : String


    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "onReceive() 시작")
        var action = intent.action

        ordId = intent.getStringExtra("ordId").toString()
        routeId = intent.getStringExtra("routeId").toString()
        nodeId = intent.getStringExtra("nodeId").toString()

        Log.d("AlarmReceiver", "액션 값: $action")
        when (action) {
            "UPDATE_DATA" -> {
                // 액션에 따른 동작 수행
                updatedArrmsg1 = intent.getStringExtra("arrmsg1")
                Log.d("AlarmReceiver", "업데이트된 데이터: $updatedArrmsg1")

                if (!updatedArrmsg1.isNullOrBlank()) {
                    updateNotification(context, updatedArrmsg1!!)
                }
            }
            "START_FOREGROUND_SERVICE" -> {
                val serviceIntent = Intent(context, MyForegroundService::class.java)
                serviceIntent.putExtra("ordId", ordId)
                serviceIntent.putExtra("routeId", routeId)
                serviceIntent.putExtra("nodeId", nodeId)
                //여기서 버전 확인해야 하는거 추가해야 함
                context.startForegroundService(serviceIntent)
                updateNotification(context, updatedArrmsg1)
            }
        }
    }

    private fun updateNotification(context: Context, updatedArrmsg1: String?) {
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
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time Related Notification")
            .setContentText("도착 예정: $updatedArrmsg1")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
