package com.example.runrun

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

// We don't use namespaces
private val ns: String? = null

class BusRouteListXmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    //파서를 인스턴스화하고 파싱 프로세스를 시작
    fun parse(inputStream: InputStream): List<ItemList> {

        inputStream.use { inputPara ->
            //inputStream이 shadow됐다고 하는데 왜 그럴까? 매개변수로 인식을 못한다.
            //그래서 inputStream을 inputPara로 이름 바꿔줌.
            val parser: XmlPullParser = Xml.newPullParser()
            //namespace를 처리하지 않도록 설정
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            //Sets the input stream the parser is going to process
            parser.setInput(inputPara, null)
            //Call next() and return event if it is START_TAG or END_TAG otherwise throw an exception
            parser.nextTag()
            //List<ItemList>를 반환한다
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<ItemList> {
        var entries = mutableListOf<ItemList>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            //Starts by looking for the 도입 부분의 tag
            //<itemList>라는 태그를 발견하면 시작함.
            if (parser.name == "itemList") {
                entries.add(readItemList(parser))
            } else {
                skip(parser)
            }
        }
        //전체 피드가 반복적으로 처리되고 나면 readFeed()는 피드에서 추출한 항목(중첩된 데이터 멤버 포함)이 들어 있는 List를 반환
        return entries
    }

    data class ItemList(var rtNm: String?, var stNm: String?, var arrmsg1: String?, var arrmsg2: String?)

    //Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    //to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readItemList(parser: XmlPullParser): ItemList {
        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        var rtNm: String? = null
        var stNm: String? = null
        var arrmsg1: String? = null
        var arrmsg2: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "rtNm" -> rtNm = readRtNm(parser)
                "stNm" -> stNm = readStNm(parser)
                "arrmsg1" -> arrmsg1 = readMsg1(parser)
                "arrmsg2" -> arrmsg2 = readMsg2(parser)
                else -> skip(parser)
            }
        }
        return ItemList(rtNm, stNm, arrmsg1, arrmsg2)
    }
    //Processes RouteName tags in the feed
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readRtNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "rtNm")
        val rtNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "rtNm")
        return rtNm
    }
    //Processes StationName tags in the feed
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readStNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "stNm")
        val stNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "stNm")
        return stNm
    }
    //Processes first arrive
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readMsg1(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arrmsg1")
        val arrmsg1 = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arrmsg1")
        return arrmsg1
    }
    //Processes second arrive
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readMsg2(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arrmsg2")
        val arrmsg2 = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arrmsg2")
        return arrmsg2
    }
    //extract text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
    // skip uninterested data
    @Throws(IOException::class, XmlPullParserException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}