package com.example.runrun

import okhttp3.Interceptor
import okhttp3.Request

// 패키지 명이랑 SHA-1를 같이 보내야 연결이 가능함
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
