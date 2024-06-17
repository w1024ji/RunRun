package com.example.runrun

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest: Request = chain.request()
        val requestWithHeaders: Request = originalRequest.newBuilder()
            .header("X-Android-Package", "com.example.runrun")
            .header("X-Android-Cert", "AC39D7C617C2D150271076FAD3459B0FD6D6A69D")
            .build()
        return chain.proceed(requestWithHeaders)
    }
}
