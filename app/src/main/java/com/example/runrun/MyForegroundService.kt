package com.example.runrun

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

// 백그라운드에서 동작하는 서비스로, 주기적으로 네트워크 요청을 수행하고 결과를 처리하여 알림을 생성하는 역할을 수행하는 서비스.
class MyForegroundService : Service() {
    private val handler = Handler(Looper.getMainLooper())

    private val channelId = "102"

    private lateinit var ord: String
    private lateinit var busId: String
    private lateinit var stId: String
    // Notification ID to be used for startForeground
    private val NOTIFICATION_ID = 3
    // 동적으로 등록할 리시버
    private val alarmReceiver = AlarmReceiver()

    override fun onCreate() {
        super.onCreate()
        Log.d("MyForegroundService" , "onCreate() 실행")

    }


    // 서비스 시작 시 호출되며, Foreground 서비스로 설정하고 주기적인 작업을 수행.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("MyForegroundService", "onStartCommand 실행")

        // SetAlarmActivity에서 받는 인텐트
        ord = intent?.getStringExtra("ordId") ?: "26"
        busId = intent?.getStringExtra("routeId") ?: "109900010"
        stId = intent?.getStringExtra("nodeId") ?: "109000052"

        Log.d("MyForegroundService", "ord, busId, stId값: $ord, $busId, $stId")

        // 동적으로 리시버 등록
        registerReceiver(alarmReceiver, IntentFilter("UPDATE_DATA"))

        // Start the background thread by posting the runnableCode to the handler
        handler.post(runnableCode)

        return START_STICKY
    }


    // Method to create a simple notification
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("MyForegroundService") // 얘가 위에 뜸
            .setContentText("서비스가 작동중 입니다")    // 얘랑 같이
            .setSmallIcon(R.drawable.ic_notification)  // Replace with your own drawable icon
            .build()
    }

    // Method to create the notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Time 관련된 Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    // 주기적인 네트워크 요청 및 알림 생성을 담당하는 Runnable 객체.
    private val runnableCode = object : Runnable {
        override fun run() {
            Log.d("MyForegroundService", "run() 실행")
            var arrmsg1 = "갱신될 내용"
            Thread {
                try {
                    val entries = loadPage(ord, busId, stId)
                    Log.d("entries값: ", entries.toString()) // [ItemList(rtNm=노원15, stNm=창동아이파크, arrmsg1=곧 도착, arrmsg2=null)]

                    // 가져온 데이터가 비어 있지 않으면 실행
                    if ((entries != null) && entries.isNotEmpty()) {
                        arrmsg1 = entries[0].arrmsg1 ?: "N/A"
                    }

                    val broadcastIntent = Intent()
                    broadcastIntent.putExtra("arrmsg1", arrmsg1)
                    sendBroadcast(broadcastIntent)

                    handler.postDelayed(this, 60000)
                } catch (e: Exception) {
                    Log.e("MyForegroundService", "RunnableCode에서 에러", e)
                }
            }.start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyForegroundService ", "onDestroy() 실행")
        // 동적으로 등록한 리시버 해제
        unregisterReceiver(alarmReceiver)
        handler.removeCallbacks(runnableCode)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d("MyForegroundService ", "onBind() 실행")
        return null
    }

    // 네트워크 통신 및 XML 파싱에 관련된 메서드
    private fun loadPage(ord: String, busRouteId: String, stId: String): List<BusRouteListXmlParser.ItemList>? {
        Log.d("MyForegroundService ", "loadPage() 시작")
        val urlString = buildBusUrl(ord, busRouteId, stId)

        return try {
            downloadXml(urlString)
        } catch (e: Exception) {
            Log.e("MyForegroundService", "Error loading page", e)
            null
        }
    }

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
            Log.e("MyForegroundService", "Connection error", e)
            null
        } catch (e: XmlPullParserException) {
            Log.e("MyForegroundService", "XML error", e)
            null
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String): List<BusRouteListXmlParser.ItemList> {
        val entries = BusRouteListXmlParser().parse(downloadUrl(urlString))
        return entries
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
//        Log.d("MyForegroundService ", "downloadUrl() 시작")
        val url = URL(urlString)
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            with(urlConnection) {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 10000
                doInput = true

                connect()

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return inputStream
                } else {
                    Log.e("MyForegroundService", "HTTP error code: $responseCode")
                }
            }
        } catch (e: SocketTimeoutException) {
            Log.e("MyForegroundService", "Connection timeout", e)
        } catch (e: IOException) {
            Log.e("MyForegroundService", "Network error", e)
        }
        return null
    }
}
