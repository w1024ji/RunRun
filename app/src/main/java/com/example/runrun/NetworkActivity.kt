package com.example.runrun


import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.concurrent.thread

class NetworkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)

        val xmlText: TextView = findViewById(R.id.xmlTextview)

        val startOrStop: String? = intent.getStringExtra("startOrStop")
        val arsId: String? = intent.getStringExtra("arsId")
        val routeId: String? = intent.getStringExtra("routeId")
        val nodeId: String? = intent.getStringExtra("nodeId")

        // default setting - 창동쌍용.성원아파트 - 노원15
        val ord: String = arsId ?: "26"
        val busRouteId: String = routeId ?: "109900010"
        val stId: String = nodeId ?: "109000052"
        Log.d("ord, busRouteId, stId의 값 : ", "$ord, $busRouteId, $stId")

        if (startOrStop == "start") {
            thread(start = true) {
                val parsedText = loadPage(ord, busRouteId, stId)
                runOnUiThread {
                    xmlText.text = parsedText
                }
            }
        }
    }

    companion object {
        // Static variables
        const val WIFI = "Wi-Fi"
        const val ANY = "Any"
    }

    private fun loadPage(ord: String, busRouteId: String, stId: String): String? {
        val urlString = buildBusUrl(ord, busRouteId, stId)

        return try {
            val parsedString = downloadXml(urlString)
            parsedString
        } catch (e: Exception) {
            Log.e("NetworkActivity", "Error loading page", e)
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

    private fun downloadXml(urls: String) : String? {
        var result: String? = try {
            loadXmlFromNetwork(urls)
        } catch (e: IOException) {
            resources.getString(R.string.connection_error)
        } catch (e: XmlPullParserException) {
            resources.getString(R.string.xml_error)
        }
        return result
        }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String): String {

        if (downloadUrl(urlString) == null){
            Log.d("NetworkActivity : ---", "loadXmlFromNetwork()에서 null 발생")
        }
        //null이 아닌 거 확인됨
        val entries:List<BusRouteListXmlParser.ItemList> = BusRouteListXmlParser().parse(downloadUrl(urlString))
        return entries.toString()
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