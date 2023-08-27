package com.example.runrun

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StopAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("StopAlarmReceiver", "Received stop alarm broadcast")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Cancel the notification with ID 1
        notificationManager.cancel(3)
        Log.d("StopAlarmReceiver", "Notification canceled")
    }
}
