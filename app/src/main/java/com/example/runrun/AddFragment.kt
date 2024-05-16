package com.example.runrun

import BusParcelable
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runrun.databinding.FragmentListBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.combine

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentListBinding

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
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        setupApplyButton()

        return binding.root
    }

    private fun setupApplyButton() {
        binding.applyButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val busInfoCollection = db.collection("busInfo")
            val stationNameInput = binding.enterStation.text.toString()
            val busNameInput = binding.enterBusName.text.toString()

            Log.d("MainActivity", "db값: $db, busInfoCollection값: $busInfoCollection")

            val query = busInfoCollection
                .whereEqualTo("정류소명", stationNameInput)
                .whereEqualTo("노선명", busNameInput)

            Log.d("MainActivity", "query값: $query")

            query.get()
                .addOnSuccessListener { querySnapshot ->
                    handleQuerySuccess(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    handleQueryFailure(exception)
                }
        }
    }
    private fun handleQuerySuccess(documents: List<DocumentSnapshot>) {
        val matchingDataList = documents.mapNotNull { it.data }
        val busDataList = ArrayList(matchingDataList.map {
            BusParcelable(
                it["ARS_ID"].toString(),
                it["NODE_ID"].toString(),
                it["ROUTE_ID"].toString(),
                it["X좌표"].toString(),
                it["Y좌표"].toString(),
                it["노선명"].toString(),
                it["순번"].toString(),
                it["정류소명"].toString()
            )
        })

        // val busDataList: ArrayList<BusParcelable!>
        val intent = Intent(requireContext(), MapViewActivity::class.java)
        intent.putExtra("busDataList", busDataList)
        Log.d("MainActivity", "MapView로 보내는 인텐트: $busDataList")
        startActivity(intent)
    }

    private fun handleQueryFailure(exception: Exception) {
        Log.d("QueryFailure", "Query failed: $exception")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}