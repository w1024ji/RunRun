import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("getInsightSatlit")
    fun getXmlList(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("sat") sat: String,
        @Query("data") data: String,
        @Query("area") area: String,
        @Query("time") time: Int
    ): Call<XmlResponse>
}
