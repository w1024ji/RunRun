package com.example.runrun

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("getInsightSatlit")
    fun getXmlList(
        @Query("serviceKey") serviceKey : String,
        @Query("numOfRows") numOfRows : Int,
        @Query("pageNo") pageNo : Int,
        @Query("sat") sat : String,
        @Query("data") data : String,
        @Query("area") ea : String,
        @Query("time") time : Int,
    ) : Call<XmlResponse>
}