import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class XmlResponse(
    @Element
    val body: MyXmlBody
)

@Xml(name = "body")
data class MyXmlBody(
    @Element
    val items: MyXmlItems
)

@Xml(name = "items")
data class MyXmlItems(
    @Element
    val item: MutableList<MyXmlItem> //item에는 내용이 많아서
)

@Xml(name = "item")
data class MyXmlItem(
    @PropertyElement
    val satImgCFiles: String? = null
)


//@Xml
//data class SatImgCFile(
//    @PropertyElement val value: String? = null
//)
