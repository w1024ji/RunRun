package com.example.runrun


import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NetworkActivity : AppCompatActivity() {
    private var startOrStop: String? = intent.getStringExtra("startOrStop")
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)
        var xmlText : TextView = findViewById(R.id.xmlTextview)
        if (startOrStop == "start"){
            var parsedText = loadPage()
            xmlText.text = parsedText
        }
    }

    companion object {

        const val WIFI = "Wi-Fi"
        const val ANY = "Any"
        const val BUS_URL = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute?ServiceKey=eR654WhVA2%2FiRXBlwnzEwvgRkDdAXPLHMwmIm3zmnYRZADkNhoWqA6lNj8QWyUWVyLLBXrrxJ6CkJ0h%2B4lCvyQ%3D%3D&stId=109000052&busRouteId=109900010&ord=26"

        // Whether there is a Wi-Fi connection.
        private var wifiConnected = true //true로 바꿔둠

        // Whether there is a mobile connection.
        private var mobileConnected = false

        // Whether the display should be refreshed.
        //var refreshDisplay = true

        // The user's current network preference setting.
        var sPref: String? = ANY //ANY로 바꿔둠
    }

    // Asynchronously downloads the XML feed from BUS API.
    // XML피드의 URL을 포함한 문자열 변수를 초기화합니다.
    // 사용자의 설정과 네트워크 연결에서 허용하는 경우 downloadXml(url) 메서드를 호출합니다.
    // 이 메서드는 피드를 다운로드하여 파싱하고 UI에 표시될 문자열 결과를 반환합니다.
    private fun loadPage(): String? {
        var parsedString:String? = null
        if (sPref.equals(ANY) && (wifiConnected || mobileConnected)) {
            parsedString = downloadXml(BUS_URL) //String을 리턴한다
            //
            //여기서 핸들러로 MainActivity UI에 있는 TextView창에 parsedString을 띄울 수 있게 구현해야 한다!!!
            //
            //
            //
            //
        } else if (sPref.equals(WIFI) && wifiConnected) {
            downloadXml(BUS_URL)
        } else {
            // Show error. 에러 시 사용자에게 무엇을 보여줄 것인가?
        }
        return parsedString
    }

    // Download XML feed from BUS API.
    //이 함수는 String?타입의 result를 리턴한다. onCreate()를 만들고 그 안에서 result가지고 Textview에 띄우고 싶다.
    private fun downloadXml(vararg urls: String) : String? {
        var result: String? = try {
            // loadXmlFromNetwork()의 반환값은 HTML string
            loadXmlFromNetwork(urls[0]) // 왜 urls[0]인지 모르겠다!!!
        } catch (e: IOException) {
            resources.getString(R.string.connection_error)
        } catch (e: XmlPullParserException) {
            resources.getString(R.string.xml_error)
        }
        return result

        }


    //downloadUrl(urlString)?의 Inputstream타입의 리턴값을 stream이라는 이름의 매개변수로 넣어서 실행
    //내가 만든 parse()에 넣는다. parse()는 List<ItemList>를 반환함
    // 이 함수의 최종 결과는 List<ItemList>를 String으로 변환해 가져온다.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String): String {
        val entries: List<BusRouteListXmlParser.ItemList> = downloadUrl(urlString)?.use { stream ->
            BusRouteListXmlParser().parse(stream)
        } ?: emptyList()
        return entries.toString()
    }

    // Given a string representation of a URL, sets up a connection and gets an input stream.
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        //URL() creates a URL object from the String representation
        val url = URL(urlString)

        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // connect() opens a communications link to the resource referenced by this URL,
            // if such a connection has not already been established.
            connect()
            inputStream//Returns an input stream that reads from this open connection
        }
    }

}