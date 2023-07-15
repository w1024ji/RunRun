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
        if (startOrStop == "start"){
            loadPage()
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
    private fun loadPage() {
        var parsedString:String?
        if (sPref.equals(ANY) && (wifiConnected || mobileConnected)) {
            parsedString = downloadXml(BUS_URL) //String을 리턴한다
            //여기서 핸들러로 MainActivity UI에 있는 TextView창에 parsedString을 띄울 수 있게 구현해야 한다!!!
        } else if (sPref.equals(WIFI) && wifiConnected) {
            downloadXml(BUS_URL)
        } else {
            // Show error. 에러 시 사용자에게 무엇을 보여줄 것인가?
        }
    }

    // Download XML feed from BUS API.
    // vararg는 가변인자이다.
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


    // Uploads XML from 버스 링크, parses it, and combines it with HTML markup. Returns HTML string.<-안 할거임
    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork(urlString: String): String {
        //downloadUrl(urlString)?의 Inputstream타입의 리턴값을 stream이라는 이름의 매개변수로 넣어서 람다 실행
        val entries: List<BusRouteListXmlParser.ItemList> = downloadUrl(urlString)?.use { stream ->
            //내가 만든 parse()에 넣는다. parse()는 List<ItemList>를 반환함
            BusRouteListXmlParser().parse(stream)
            //emptyList()는 Returns an empty read-only list. The returned list is serializable (JVM).
            //null이라면 emptyList()를 반환하라는 건가? 이 부분은 잘 모르겠다.
        } ?: emptyList()
        return entries.toString()

    }

    // Given a string representation of a URL, sets up a connection and gets an input stream.
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        //URL()함수는 creates a URL object from the String representation
        val url = URL(urlString)
        //openConnection()함수는 Returns a URLConnection instance that represents a connection to the remote object referred to by the URL
        //여기서 as? 연산자는 어떤 값을 HttpURLConnection 타입으로 캐스트를 하고, 만약 캐스트 할 수 없으면 null을 반환하게 됩니다.
        //.run은 The context object is available as a receiver (this: HttpURLConnection)이다.
        //lambda result를 리턴한다
        //run is useful when your lambda both initializes objects & computes the return value.
        return (url.openConnection() as? HttpURLConnection)?.run {
            // Sets the read timeout to a specified timeout, in milliseconds.
            // A non-zero value specifies the timeout when reading from Input stream when a connection is established to a resource.
            readTimeout = 10000
            //Sets a specified timeout value, in milliseconds, to be used when opening a communications link to the resource referenced by this URLConnection.
            // If the timeout expires before the connection can be established, a java.net.SocketTimeoutException is raised.
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query.
            //connect() 함수는 Opens a communications link to the resource referenced by this URL,
            // if such a connection has not already been established.
            connect()
            //Returns an input stream that reads from this open connection
            inputStream
        }
    }

}