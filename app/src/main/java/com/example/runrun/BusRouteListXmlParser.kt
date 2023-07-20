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
            if (parser.name == "comMsgHeader") {

                entries.add(readItemList(parser))
            } else {
                Log.d("readFeed()에서 else경우 발생 : ", "skip(parser)") // 발생 안 함
                //skip(parser)
            }
        }

        return entries
    }

    data class ItemList(
        var rtNm: String?,
        var stNm: String?,
        var arrmsg1: String?,
        var arrmsg2: String?
    )

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readItemList(parser: XmlPullParser): ItemList {

        var rtNm: String? = null
        var stNm: String? = null
        var arrmsg1: String? = null
        var arrmsg2: String? = null
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "rtNm" -> rtNm = read(parser)
                    "stNm" -> stNm = read(parser)
                    "arrmsg1" -> arrmsg1 = read(parser)
                    "arrmsg2" -> arrmsg2 = read(parser)
                    else -> continue
                }
            } else {
                continue
            }
        }
        return ItemList(rtNm, stNm, arrmsg1, arrmsg2)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun read(parser: XmlPullParser): String {
        var brought = parser.nextText()
        parser.next()
        return brought
    }
}