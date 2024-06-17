package com.example.runrun


data class YouTubeSearchResponse(
    val items: List<VideoItem>
)

data class VideoItem(
    val id: VideoId,
    val snippet: VideoSnippet
)

data class VideoId(
    val kind: String,
    val videoId: String
)

data class VideoSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: VideoThumbnails,
    val channelTitle: String,
    val liveBroadcastContent: String
)

data class VideoThumbnails(
    val default: ThumbnailDetails,
    val medium: ThumbnailDetails,
    val high: ThumbnailDetails
)

data class ThumbnailDetails(
    val url: String,
    val width: Int,
    val height: Int
)
