package com.example.runrun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.runrun.databinding.FragmentSatelliteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlin.text.indexOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SatelliteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SatelliteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    // binding
    private lateinit var binding : FragmentSatelliteBinding

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
        binding = FragmentSatelliteBinding.inflate(inflater, container, false)


        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR).toString()
        val month = (calender.get(Calendar.MONTH) + 1).toString()
        val day = calender.get(Calendar.DAY_OF_MONTH).toString()
        val currentDate = "${year}0$month$day"
        Log.d("SatelliteFragment", "현재 년월일 값: $currentDate") // e.g) 20240515

        val call: Call<XmlResponse> = RetrofitConnection.xmlNetworkService.getXmlList(
            "asPqYsCGu0sX+rVYZ4ldsfkjQ1XBX2tvvtIOS8Wl2gbdG4wIDzLlmWdFgZ64SZ61YqPDqjb0OKXe8LB8W7XMmw==", // Decoding
            1,
            10,
            "g2",
            "ir105",
            "ko",
            currentDate.toInt()
        )

        call.enqueue(object : Callback<XmlResponse> {
            override fun onResponse(call: Call<XmlResponse>, response: Response<XmlResponse>) {
                if(response.isSuccessful){
                    Log.d("SatelliteFragment", "onResponse(): ${call.request()}")
                    Log.d("SatelliteFragment", "response.body()값: ${response.body()}")
                    val desiredTime = "202405260400"
                    val desiredTimeRegex = Regex(desiredTime)
                    val body = response.body()?.body

                    if (body?.items != null) {
                        Log.d("SatelliteFragment", "body?items가 null이 아니라면")
                        val imageUrls = body.items.item // Use list directly

                        for (imageUrl in imageUrls) {
                            if (desiredTimeRegex.matches(imageUrl.toString())) {
                                Log.d("SatelliteFragment", "시간에 맞는 png 선택")
                                Glide.with(binding.root)
                                    .load(imageUrl)
                                    .override(500, 500)
                                    .into(binding.satelliteImageView)
                                break
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<XmlResponse>, t: Throwable) {
                Log.d("SatelliteFragment", "onFailure ${call.request()}", t)
            }
        })


        return binding.root
    } // onCreateView()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SatelliteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SatelliteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}