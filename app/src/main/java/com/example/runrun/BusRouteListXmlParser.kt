package com.example.runrun

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

// We don't use namespaces
private val ns: String? = null

class BusRouteListXmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream?): List<ItemList> {

        inputStream.use { inputPara ->

            val parser: XmlPullParser = Xml.newPullParser()
            println("parse()의 Xml.newPullParser()로 가져온 parser 값 : $parser") //성공
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputPara, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<ItemList> {
        var entries = mutableListOf<ItemList>() // Returns an empty new MutableList.
        parser.require(XmlPullParser.START_TAG, ns, "ServiceResult") // 최상단 태그 <ServiceResult>
        while (parser.next() != XmlPullParser.END_DOCUMENT) { // END_TAG에서 END_DOCUMENT로 수정함
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "itemList") { // 이 if절을 아예 통과를 못 하는 거 같은데
                entries.add(readItemList(parser)) // 여기서 문제가 생기나??
                println("entries.add(readItemList(parser))직후 entries 상태 : $entries")
            } else {
                Log.d("readFeed()에서 else경우 발생 : ", "skip(parser)")
                skip(parser)
            }
        }
        //전체 피드가 반복적으로 처리되고 나면 readFeed()는 피드에서 추출한 항목(중첩된 데이터 멤버 포함)이 들어 있는 List를 반환
        println("readFeed()의 entries 리턴값 직전 : $entries") // [ ]
        return entries
    }

    data class ItemList(var rtNm: String?, var stNm: String?, var arrmsg1: String?, var arrmsg2: String?)

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readItemList(parser: XmlPullParser): ItemList {
        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        var rtNm: String? = null
        var stNm: String? = null
        var arrmsg1: String? = null
        var arrmsg2: String? = null
        println("read함수들 시작하기 전 null 상태의 변수들 : $rtNm, $stNm, $arrmsg1, $arrmsg2") // 아예 안 나온다
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
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
        println("readItemList()의 리턴 직전 값: ${ItemList(rtNm, stNm, arrmsg1, arrmsg2)}") // 아예 안 나온다
        return ItemList(rtNm, stNm, arrmsg1, arrmsg2)
    }
    //Processes RouteName tags in the feed
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readRtNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "rtNm")
        var rtNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "rtNm")
        println("readRtNm()의 리턴 직전 값 : $rtNm")
        return rtNm
    }
    //Processes StationName tags in the feed
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readStNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "stNm")
        var stNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "stNm")
        println("readStNm()의 리턴 직전 값 : $stNm")
        return stNm
    }
    //Processes first arrive
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readMsg1(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arrmsg1")
        var arrmsg1 = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arrmsg1")
        println("readMsg1()의 리턴 직전 값 : $arrmsg1")
        return arrmsg1
    }
    //Processes second arrive
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readMsg2(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arrmsg2")
        var arrmsg2 = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arrmsg2")
        println("readMsg2()의 리턴 직전 값 : $arrmsg2")
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
        println("readText()의 리턴 직전 값 : $result")
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