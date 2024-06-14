package com.example.runrun

import com.tickaroo.tikxml.TikXml
import retrofit2.Retrofit
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory

class RetrofitConnection {

    companion object {
        private const val BASE_URL = "http://apis.data.go.kr/1360000/SatlitImgInfoService/"

        val parser = TikXml.Builder()
            .exceptionOnUnreadXml(false)  // Enable exceptions for unread XML
            .build()

        val xmlRetrofit : Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()
        }

        val xmlNetworkService : NetworkService by lazy {
            xmlRetrofit.create(NetworkService::class.java)
        }
    }
}
