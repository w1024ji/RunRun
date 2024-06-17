package com.example.runrun

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
This file will contain the singleton pattern to ensure that only one instance of the Retrofit client is created,
which is used throughout your app to make network requests.
 */

object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val instance: YouTubeApiService by lazy {
        retrofit.create(YouTubeApiService::class.java)
    }
}
