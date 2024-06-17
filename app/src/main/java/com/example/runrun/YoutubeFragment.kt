package com.example.runrun

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runrun.databinding.FragmentYoutubeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.OutputStreamWriter

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
    // binding
    lateinit var binding : FragmentYoutubeBinding
    // 사용자가 서치한 키워드
    lateinit var keyWord : String
    private var historyIndex = 0  // Index to keep track of the current history item

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
        binding = FragmentYoutubeBinding.inflate(inflater, container, false)

        val adapter = VideoAdapter()
        binding.videosRecyclerView.adapter = adapter
        binding.videosRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.searchButton.setOnClickListener {
            if(binding.searchEditText.text.isNotEmpty()){         // 비어있지 않을 때만
                keyWord = binding.searchEditText.text.toString() // history 구현 중..
                saveHistory(keyWord)
            }
            performSearch(binding.searchEditText.text.toString(), adapter)
        }

        // history 버튼을 누르면 제일 최근에 검색한 이력을 searchEditText에 올리기
        binding.historyButton.setOnClickListener {
            binding.searchEditText.setText(getRecentHistory())
        }

        return binding.root
    } // onCreateView()

    private fun saveHistory(keyWord: String) {
        val context = requireContext()
        val file = File(context.filesDir, "test_History.txt")

        // Check if the file exists, create if not
        if (!file.exists()) {
            file.createNewFile()
        }

        // Read existing keywords from the file
        val existingKeywords = LinkedHashSet(file.readLines())

        // Add the new keyword if it's not already in the set
        if (keyWord !in existingKeywords) {
            if (existingKeywords.size >= 5) {
                // If there are already 5 keywords, remove the oldest one
                val iterator = existingKeywords.iterator()
                if (iterator.hasNext()) {
                    iterator.next() // Move to the first
                    iterator.remove() // Remove the oldest keyword
                }
            }
            existingKeywords.add(keyWord)
        }

        // Write all keywords back to the file
        file.writeText(existingKeywords.joinToString("\n"))
    }

    private fun getRecentHistory(): String? {
        val file = File(requireContext().filesDir, "test_History.txt")
        val keywords = file.readLines()
        if (keywords.isEmpty()) return null

        // Calculate index to access the list from the end to the beginning
        val reversedIndex = keywords.size - 1 - historyIndex
        val currentKeyword = keywords.getOrNull(reversedIndex)
        historyIndex = (historyIndex + 1) % keywords.size
        return currentKeyword
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