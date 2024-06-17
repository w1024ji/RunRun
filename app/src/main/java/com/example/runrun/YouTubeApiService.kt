package com.example.runrun

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/*
This file will define the interface that Retrofit uses to make HTTP requests to the YouTube Data API.
 */

interface YouTubeApiService {
    @GET("search")
    fun searchVideos(
        @Query("part") part: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 3, // Fetch only the first three results
        @Query("key") apiKey: String
    ): Call<YouTubeSearchResponse>
}

