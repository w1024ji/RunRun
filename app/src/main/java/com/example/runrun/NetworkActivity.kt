package com.example.runrun


import android.content.Intent
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

        val ordId: String? = intent.getStringExtra("ordId")
        val routeId: String? = intent.getStringExtra("routeId")
        val nodeId: String? = intent.getStringExtra("nodeId")

        // default setting - 창동쌍용.성원아파트 - 노원15
        val ord: String = ordId ?: "26"
        val busRouteId: String = routeId ?: "109900010"
        val stId: String = nodeId ?: "109000052"
        Log.d("ord, busRouteId, stId의 값 : ", "$ord, $busRouteId, $stId")


        thread(start = true) {
            val entries = loadPage(ord, busRouteId, stId)
            runOnUiThread {
                if (entries != null) {
                    val intent = Intent(this@NetworkActivity, ClientInputActivity::class.java)
                    intent.putParcelableArrayListExtra("itemList", ArrayList(entries))
                    startActivity(intent)
                } else {
                    // Handle error or no data scenario
                }
            }
        }
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