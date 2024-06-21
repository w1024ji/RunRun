package com.example.runrun

import XmlResponse
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            BuildConfig.DECODING_SERVICE_KEY, // Decoding
            1,
            10,
            "g2",
            "ir105",
            "ko",
            currentDate.toInt()
        )

        call.enqueue(object : Callback<XmlResponse> {
            override fun onResponse(call: Call<XmlResponse>, response: Response<XmlResponse>) {
                if (response.isSuccessful) {
                    val items = response.body()?.body?.items?.item
                    if (!items.isNullOrEmpty()) {
                        // Assuming the list is sorted with the most recent last, grab the last image URL
                        val imageFiles = items.firstOrNull()?.satImgCFiles
                        Log.d("SatelliteFragment", "imageFiles 값: $imageFiles")

                        val mostRecentImageUrl = imageFiles?.lastOrNull()?.value
                        Log.d("SatelliteFragment", "mostRecentImageUrl 값: $mostRecentImageUrl")

                        // Load the most recent image URL into the ImageView using Glide
                        if (mostRecentImageUrl != null) {
                            Glide.with(requireContext())
                                .load(mostRecentImageUrl)
                                .centerCrop()
                                .into(binding.satelliteImageView)
                        } else {
                            Toast.makeText(context, "No recent image URL available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("SatelliteFragment", "No items found in response")
                        Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("SatelliteFragment", "Response was not successful")
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