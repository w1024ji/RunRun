package com.example.ch18_image

import com.example.ch18_image.XmlResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// https://apis.data.go.kr/B553748/CertImgListServiceV3/getCertImgListServiceV3?serviceKey=asPqYsCGu0sX%2BrVYZ4ldsfkjQ1XBX2tvvtIOS8Wl2gbdG4wIDzLlmWdFgZ64SZ61YqPDqjb0OKXe8LB8W7XMmw%3D%3D&prdlstReportNo=201305230193&prdlstNm=%ED%95%B4%EB%82%A8%EC%97%90%EC%84%9C%EB%A7%8C%EB%93%A0%EB%B0%98%EC%8B%9C%EA%B3%A0%EA%B5%AC%EB%A7%88&returnType=xml&pageNo=1&numOfRows=10&prdkind=%EC%84%9C%EB%A5%98%EA%B0%80%EA%B3%B5%ED%92%88&manufacture=%ED%95%B4%EB%82%A8%EA%B3%A0%EA%B5%AC%EB%A7%88%EC%8B%9D%ED%92%88%EC%A3%BC%EC%8B%9D%ED%9A%8C%EC%82%AC&allergy=%EB%8F%BC%EC%A7%80%EA%B3%A0%EA%B8%B0,%20%EB%B0%80%20%ED%95%A8%EC%9C%A0

interface NetworkService {
    @GET("getCertImgListServiceV3")
    fun getXmlList(
        @Query("prdlstNm") name:String,
        @Query("pageNo") pageNo:Int,
        @Query("numOfRows") numOfRows:Int,
        @Query("returnType") returnType:String,
        @Query("serviceKey") apiKey:String
    ) : Call<XmlResponse>
}