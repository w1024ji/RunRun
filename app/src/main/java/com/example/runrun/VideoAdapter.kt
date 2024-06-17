package com.example.runrun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoAdapter(private var videos: List<VideoItem> = listOf()) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    fun updateData(newVideos: List<VideoItem>) {
        videos = newVideos
        notifyDataSetChanged()  // Notify the RecyclerView to re-render the data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.bind(video)
    }

    override fun getItemCount() = videos.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.videoTitleTextView)

        fun bind(video: VideoItem) {
            titleTextView.text = video.snippet.title
        }
    }
}
