package com.example.runrun

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MyForegroundService : Service() {
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var ord: String
    private lateinit var busId: String
    private lateinit var stId: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ord = intent?.getStringExtra("ordId") ?: "26"
        busId = intent?.getStringExtra("routeId") ?: "109900010"
        stId = intent?.getStringExtra("nodeId") ?: "109000052"

        Log.d("MyForegroundService", "Values: $ord, $busId, $stId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "foreground_id",
                "Foreground Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "MyForegroundService Notification"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "foreground_id")
            .setSmallIcon(R.drawable.foreground_noti)
            .setContentTitle("My Foreground Service")
            .setContentText("Service is running...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        handler.post(runnableCode)

        return START_STICKY
    }

    private val runnableCode = object : Runnable {
        override fun run() {
            val entries = loadPage(ord, busId, stId)
            if (entries != null && entries.isNotEmpty()) {
                val firstEntry = entries[0]
                val rtNm = firstEntry.rtNm ?: "N/A"
                val stNm = firstEntry.stNm ?: "N/A"
                val arrmsg1 = firstEntry.arrmsg1 ?: "N/A"

                val contentText = "Route: $rtNm, Station: $stNm, Arrival: $arrmsg1"

                val updatedNotification: Notification = NotificationCompat.Builder(this@MyForegroundService, "foreground_id")
                    .setSmallIcon(R.drawable.foreground_noti)
                    .setContentTitle("Bus Arrival")
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)
            }

            handler.postDelayed(this, 60000)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableCode)
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }

    private fun loadPage(ord: String, busRouteId: String, stId: String): List<BusRouteListXmlParser.ItemList>? {
        val urlString = buildBusUrl(ord, busRouteId, stId)

        return try {
            downloadXml(urlString)
        } catch (e: Exception) {
            Log.e("NetworkActivity", "Error loading page", e)
            null
        }
    }

    // Below are from NetworkActivity.kt(soon be erased)

    private fun buildBusUrl(ord: String, busRouteId: String, stId: String): String {
        return "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute" +
                "?ServiceKey=${BuildConfig.SERVICE_KEY}" +
                "&stId=$stId" +
                "&busRouteId=$busRouteId" +
                "&ord=$ord"
    }

    private fun downloadXml(urls: String): List<BusRouteListXmlParser.ItemList>? {
        val result: List<BusRouteListXmlParser.ItemList>? = try {
            loadXmlFromNetwork(urls)
        } catch (e: IOException) {
            Log.e("NetworkActivity", "Connection error", e)
            null
        } catch (e: XmlPullParserException) {
            Log.e("NetworkActivity", "XML error", e)
            null
        }
        return result
    }


    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String): List<BusRouteListXmlParser.ItemList> {
        val entries = BusRouteListXmlParser().parse(downloadUrl(urlString))
        return entries
    }


    // Given a string representation of a URL, sets up a connection and gets an input stream.
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {

        val url = URL(urlString)

        val urlConnect = url.openConnection() as HttpURLConnection
        urlConnect.requestMethod = "GET"
        urlConnect.connectTimeout = 15000
        urlConnect.readTimeout = 10000
        urlConnect.doInput = true
        urlConnect.connect()
        var result : InputStream? = try {
            urlConnect.inputStream //Returns an input stream that reads from this open connection
        }catch (e: SocketTimeoutException){
            null
        }
        return result
    }
}
