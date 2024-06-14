package com.example.runrun

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import retrofit2.http.Body

@Xml(name = "response")
data class XmlResponse(
    @Element
    val header: Header,
    @Element
    val body: MyXmlBody
)

@Xml
data class Header(
    @PropertyElement val resultCode: String?,
    @PropertyElement val resultMsg: String?
)

@Xml(name = "body")
data class MyXmlBody(
    @PropertyElement
    val dataType: String?,  // This line maps the <dataType> XML element
    @Element
    val items: MyXmlItems
)

@Xml(name = "items")
data class MyXmlItems(
    @Element
    val item: MutableList<MyXmlItem>
)

@Xml(name = "item")
data class MyXmlItem(
    @Element(name = "satImgC-file")
    val satImgCFiles: MutableList<SatImgCFile>
)

@Xml(name = "satImgC-file")
data class SatImgCFile(
    @PropertyElement
    val url: String?  // This should match the content inside <satImgC-file>
) {
    constructor() : this(null)
}

