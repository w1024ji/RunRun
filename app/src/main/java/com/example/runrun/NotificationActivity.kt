package com.example.runrun

import android.app.*
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.util.*
import android.content.Context
import android.os.Bundle
import android.util.Log


class NotificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        scheduleNotification()
    }

    companion object {
        // Building the Notification
        fun buildNotification(context: Context): Notification {
            val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_notification) // replace with your icon
                .setContentTitle("Time-based Notification")
                .setContentText("This is your scheduled notification.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            return builder.build()
        }
    }

    // Setting up the Notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Time-based Notification"
            val descriptionText = "Channel for time-based notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Creates a notification channel
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }

            // Return the handle to a system-level service by name.
            // The class of the returned object varies by the requested name.
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.d("createNotificationChannel()- notificationManager 값: ", "$notificationManager")
            notificationManager.createNotificationChannel(channel)
        }
    }


    // Setting up the Alarm
    private fun scheduleNotification() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val notificationIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

}