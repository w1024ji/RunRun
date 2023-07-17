package com.example.runrun


import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.math.log

class NetworkActivity : AppCompatActivity() {

    //private val serviceKey = BuildConfig.SERVICE_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)
        val startOrStop: String? = intent.getStringExtra("startOrStop")
        val xmlText : TextView = findViewById(R.id.xmlTextview)
        if (startOrStop == "start"){
            var parsedText = loadPage()
            xmlText.setText(parsedText)
        }
    }

    companion object {
        //val serviceKey = "serviceKey ${BuildConfig.serviceKey}"

        const val WIFI = "Wi-Fi"
        const val ANY = "Any"
        const val BUS_URL = "..."

        // Whether there is a Wi-Fi connection.
        private var wifiConnected = true //true로 바꿔둠

        // Whether there is a mobile connection.
        private var mobileConnected = false

        // Whether the display should be refreshed.
        //var refreshDisplay = true

        // The user's current network preference setting.
        var sPref: String? = ANY //ANY로 바꿔둠
    }

    private fun loadPage(): String? {
        var parsedString:String? = null
        if (sPref.equals(ANY) && (wifiConnected || mobileConnected)) {
            parsedString = downloadXml(BUS_URL) // 연습이라 사실상 여기서 끝나게 만들었다. 일단은 말이지
            if (parsedString == null){
                Log.d("NetworkActivity : ---", "loadPage()에서 parsedString이 null을 받음")
            }

        } else if (sPref.equals(WIFI) && wifiConnected) {
            downloadXml(BUS_URL)
        } else {
            // Show error. 에러 시 사용자에게 무엇을 보여줄 것인가?
        }
        return parsedString
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
//            println("loadXmlFromNetwork()에서 null 발생")
            Log.d("NetworkActivity : ---", "loadXmlFromNetwork()에서 null 발생")
        }
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