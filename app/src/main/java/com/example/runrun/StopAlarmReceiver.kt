package com.example.runrun

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StopAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("StopAlarmReceiver", "OnReceive() 실행")
        Log.d("StopAlarmReceiver", "action 값: ${intent.action}")

        // MyForegroundService 종료
        val serviceIntent = Intent(context, MyForegroundService::class.java)
        context.stopService(serviceIntent)
        Log.d("StopAlarmReceiver", "MyForegroundService 서비스 종료")

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(3)
        Log.d("StopAlarmReceiver", "Notification 취소됨")
    }
}
