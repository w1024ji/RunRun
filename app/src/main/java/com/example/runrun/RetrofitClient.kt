package com.example.runrun

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * YouTube API와의 통신을 위한 싱글톤 Retrofit 클라이언트 객체를 제공한다.
 * 이 객체는 YouTube 데이터에 접근하여 앱에서 사용할 수 있도록 한다
 */
object RetrofitClient {
    // YouTube API의 기본 URL
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    // OkHttpClient 인스턴스를 생성하고, HTTP 요청 헤더를 추가하는 인터셉터를 설정
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor()) // 패키지 네임이랑 SHA-1 키 넣기
        .build()

    // Retrofit 빌더를 사용하여 Retrofit 인스턴스를 생성
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // API의 기본 URL 설정
        .client(okHttpClient)  // 커스텀 HTTP 클라이언트 사용
        .addConverterFactory(GsonConverterFactory.create())  // JSON 응답을 객체로 변환하기 위한 컨버터 팩토리 추가
        .build()

    // Retrofit 인스턴스를 사용하여 YouTubeApiService 인터페이스의 구현체를 생성
    // 이 구현체는 API의 실제 요청들을 수행한다
    val instance: YouTubeApiService by lazy {
        retrofit.create(YouTubeApiService::class.java)
    }
}

