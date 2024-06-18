package com.example.runrun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 * RecyclerView 어댑터 클래스로, YouTube 동영상 목록을 관리하고 표시한다
 */
class VideoAdapter(private var videos: List<VideoItem> = listOf()) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    fun updateData(newVideos: List<VideoItem>) {
        videos = newVideos
        notifyDataSetChanged()  // 데이터 변경을 RecyclerView에 알림
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.bind(video)
    }
    override fun getItemCount() = videos.size // 비디오 목록의 개수

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LifecycleObserver {
        private val titleTextView: TextView = itemView.findViewById(R.id.videoTitleTextView)
        private val youtubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)

        /**
         * 비디오 항목을 뷰에 바인딩. YouTube Player를 초기화하고 비디오를 준비!
         * @param video 표시할 비디오 항목
         */
        fun bind(video: VideoItem) {
            titleTextView.text = video.snippet.title  // 비디오 제목 설정
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(video.id.videoId, 0f)  // 준비되면 비디오를 로드
                }
            })
        }
    }
}
