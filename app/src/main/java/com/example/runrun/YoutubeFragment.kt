package com.example.runrun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**

A simple [Fragment] subclass.

Use the [YoutubeFragment.newInstance] factory method to

create an instance of this fragment.
 */
class YoutubeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_youtube, container, false)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val videosRecyclerView = view.findViewById<RecyclerView>(R.id.videosRecyclerView)
        val adapter = VideoAdapter()
        videosRecyclerView.adapter = adapter
        videosRecyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.searchButton).setOnClickListener {
            performSearch(searchEditText.text.toString(), adapter)
        }

        return view
    }

    private fun performSearch(query: String, adapter: VideoAdapter) {
        // Call the YouTube API using Retrofit to search for videos based on the user's query
        RetrofitClient.instance.searchVideos(
            part = "snippet",
            query = query,
            maxResults = 3, // Limit results to 3 to only fetch the first three videos
            apiKey = BuildConfig.GOOGLE_API
        ).enqueue(object : Callback<YouTubeSearchResponse> {
            override fun onResponse(
                call: Call<YouTubeSearchResponse>,
                response: Response<YouTubeSearchResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("YoutubeFragment", "response.isSuccessful 성공적")
                    response.body()?.items?.let {
                        // Update the adapter with the list of video items
                        adapter.updateData(it)
                    }
                } else {
                    Log.d("YoutubeFragment", "response 실패..")
                    Log.e("YoutubeFragment", "API Error-Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<YouTubeSearchResponse>, t: Throwable) {
                Log.e("YoutubeFragment", "onFailure()-Error fetching data: ${t.localizedMessage}")
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeatherFragment.
         */
// TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YoutubeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}