package com.example.runrun

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

// 백그라운드에서 동작하는 서비스로, 주기적으로 네트워크 요청을 수행하고 결과를 처리하여 알림을 생성하는 역할을 수행하는 서비스.
class MyForegroundService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var ord : String
    private lateinit var busId : String
    private lateinit var stId : String
    private lateinit var notiNm : String
    // 동적으로 등록할 리시버
    private val alarmReceiver = AlarmReceiver()

    override fun onCreate() {
        super.onCreate()
        Log.d("MyForegroundService" , "onCreate() 실행")
        // 동적으로 리시버 등록
        registerReceiver(alarmReceiver, IntentFilter("UPDATE_DATA"))
    }

    // 서비스 시작 시 호출되며, Foreground 서비스로 설정하고 주기적인 작업을 수행.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyForegroundService", "onStartCommand 실행")
        // SetAlarmActivity에서 받는 인텐트
        ord = intent?.getStringExtra("ordId") ?: "null"
        busId = intent?.getStringExtra("routeId") ?: "null"
        stId = intent?.getStringExtra("nodeId") ?: "null"
        notiNm = intent?.getStringExtra("notiNm") ?: "null"

        Log.d("MyForegroundService", "받은 ord, busId, stId값: $ord, $busId, $stId")
        createMinimalNotification()
        handler.post(runnableCode)
        return START_STICKY
    }

    // Create a minimal notification to satisfy Android 8.0+ requirements
    private fun createMinimalNotification() {
        Log.d("MyForegroundService", "createMinimalNotification() 실행")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "100"
            val channelName = "Minimal Channel"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MIN) // INPORTANCE_MIN으로 바꿔봄
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.notification_context_title))
                .setContentText(getString(R.string.notification_context_text))
                .setSmallIcon(R.drawable.ic_notification)
                .build()

            startForeground(1, notification)
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
                    if ((entries != null) && entries.isNotEmpty()) {
                        arrmsg1 = entries[0].arrmsg1 ?: "N/A"
                    }
                    // action이 UPDATE_DATA인 브로드캐스트를 데이터와 함께 보낸다
                    val broadcastIntent = Intent()
                    broadcastIntent.action = "UPDATE_DATA"
                    broadcastIntent.putExtra("arrmsg1", arrmsg1)
                    broadcastIntent.putExtra("notiNm", notiNm)
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
//        Log.d("MyForegroundService ", "loadPage() 시작")
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
