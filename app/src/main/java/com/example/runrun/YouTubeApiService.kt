package com.example.runrun

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * YouTube 데이터 API와 통신하기 위한 인터페이스
 * 이 인터페이스는 Retrofit 라이브러리에 의해 구현되며, YouTube 동영상 검색 기능을 제공한다.
 */
interface YouTubeApiService {
    /**
     * YouTube API를 사용하여 동영상을 검색하는 메서드.
     *
     * @param part 요청할 데이터의 특정 부분을 지정. 일반적으로 'snippet'이 사용되며, 동영상의 기본적인 정보를 포함
     * @param query 검색할 텍스트 쿼리. 사용자가 입력한 검색어를 이 파라미터로 전달
     * @param type 검색할 유형을 지정. 기본적으로 'video'로 설정되어 있어 동영상만 검색
     * @param maxResults 반환받을 최대 결과 수를 지정. 기본값은 3
     * @param apiKey YouTube API 접근을 위한 API 키
     *
     * @return Call<YouTubeSearchResponse> 객체를 반환. 이 객체는 비동기적으로 API 응답을 처리하는 데 사용
     */
    @GET("search")
    fun searchVideos(
        @Query("part") part: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 3, // 최대 3개의 결과만 가져오도록 설정
        @Query("key") apiKey: String
    ): Call<YouTubeSearchResponse>
}

