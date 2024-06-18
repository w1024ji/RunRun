package com.example.runrun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runrun.databinding.FragmentYoutubeBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
    private var historyIndex = 0  // 현재 검색 이력의 인덱스를 추적
    private var file = File(requireContext().filesDir, "test_History.txt")

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
            if(binding.searchEditText.text.isNotEmpty()){      // 비어있지 않을 때만 검색 키워드 저장
                keyWord = binding.searchEditText.text.toString()
                saveHistory(keyWord)
            }
            performSearch(binding.searchEditText.text.toString(), adapter) // 최근 검색 키워드 불러오기
        }

        // history 버튼을 누르면 제일 최근에 검색한 이력을 searchEditText에 올리기
        binding.historyButton.setOnClickListener {
            binding.searchEditText.setText(getRecentHistory())
        }

        return binding.root
    } // onCreateView()

    private fun saveHistory(keyWord: String) {
        if (!file.exists()) {
            file.createNewFile()
        }
        val existingKeywords = LinkedHashSet(file.readLines()) // 기존 키워드 읽기
        if (keyWord !in existingKeywords) {
            if (existingKeywords.size >= 5) {
                val iterator = existingKeywords.iterator()
                if (iterator.hasNext()) {
                    iterator.next() // 첫 번째 키워드
                    iterator.remove() // 가장 오래된 키워드 삭제
                }
            }
            existingKeywords.add(keyWord)
        }
        file.writeText(existingKeywords.joinToString("\n")) // 키워드 저장
    }

    private fun getRecentHistory(): String? {
        val keywords = file.readLines()
        if (keywords.isEmpty()) return null

        val reversedIndex = keywords.size - 1 - historyIndex
        val currentKeyword = keywords.getOrNull(reversedIndex)
        historyIndex = (historyIndex + 1) % keywords.size // 이력 인덱스 업데이트
        return currentKeyword
    }

    // query: 사용자가 입력한 검색 뭐리. adapter: 비디오 목록을 표시하는 데 사용되는 어댑터
    private fun performSearch(query: String, adapter: VideoAdapter) {
        // RetrofitClient를 사용하여 YouTube API에 검색 요청을 보내기
        RetrofitClient.instance.searchVideos(
            part = "snippet",
            query = query,
            maxResults = 3,
            apiKey = BuildConfig.GOOGLE_API
        ).enqueue(object : Callback<YouTubeSearchResponse> {
            override fun onResponse(
                call: Call<YouTubeSearchResponse>,
                response: Response<YouTubeSearchResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("YoutubeFragment", "response.isSuccessful 성공적")
                    response.body()?.items?.let {
                        adapter.updateData(it)
                    }
                } else {
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