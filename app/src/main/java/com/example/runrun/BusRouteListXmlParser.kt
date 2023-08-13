package com.example.runrun

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

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
        var entries = mutableListOf<ItemList>()
        parser.require(XmlPullParser.START_TAG, ns, "ServiceResult")
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "comMsgHeader") {
                entries.add(readItemList(parser))
            } else {
                Log.d("readFeed()", "else occur")
            }
        }

        return entries
    }

    data class ItemList(
        var rtNm: String?,
        var stNm: String?,
        var arrmsg1: String?,
        var arrmsg2: String?
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(rtNm)
            parcel.writeString(stNm)
            parcel.writeString(arrmsg1)
            parcel.writeString(arrmsg2)
        }

        companion object CREATOR : Parcelable.Creator<ItemList> {
            override fun createFromParcel(parcel: Parcel): ItemList {
                return ItemList(parcel)
            }

            override fun newArray(size: Int): Array<ItemList?> {
                return arrayOfNulls(size)
            }
        }
    }

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
            }
            else {
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