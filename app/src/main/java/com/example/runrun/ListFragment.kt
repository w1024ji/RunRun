package com.example.runrun

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runrun.databinding.FragmentListBinding
import com.google.firebase.firestore.DocumentReference
import java.text.SimpleDateFormat

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
    // sharedPreferences를 위한 변수
    lateinit var sharedPreference : SharedPreferences
    // binding
    lateinit var binding : FragmentListBinding
    private var datas: MutableList<NotificationItem> = mutableListOf()
    private var uri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        // 리사이클 색상 변경
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val recycle_color = sharedPreference.getString("recycle_color", "#EF9797")

        binding.recyclerView.setBackgroundColor(Color.parseColor(recycle_color))
        // 플로팅 버튼 색상 변경
        val FAB_color = sharedPreference.getString("FAB_color", "#EF9797")
        binding.mainFab.setBackgroundColor(Color.parseColor(FAB_color))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)

        // Retrieve arguments from the bundle
        Log.d("ListFragment", "arguments값: $arguments")
        // Retrieve arguments from the bundle
        arguments?.let {
            val notiNm = it.getString("notiNm")
            val busNm = it.getString("busNm")
            val staNm = it.getString("staNm")
            val selectedDays = it.getString("selectedDays")
            val whenToWhen = it.getString("whenToWhen")
            uri = (it.getString("uri")?.toUri() ?: "") as Uri // 이미지 업로드 구현 중...
            Log.d("ListFragment", "onCreateView-arguments.let{}에서 uri값: $uri")

            if (notiNm != null && busNm != null && staNm != null && selectedDays != null && whenToWhen != null) {
//                val notificationItem = NotificationItem(notiNm, busNm, staNm, selectedDays, whenToWhen)
//                datas.add(notificationItem)

                val fireData = mapOf(
                    "notiNm" to notiNm,
                    "busNm" to busNm,
                    "staNm" to staNm,
                    "selectedDays" to selectedDays,
                    "whenToWhen" to whenToWhen,
                )
                // Firestore DB에 저장하기
                MyApplication.db.collection("bus_notifications")
                    .add(fireData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "데이터 저장 성공!", Toast.LENGTH_LONG).show()
                        Log.d("ListFragment", "데이터 저장 성공!!!")
                        uploadImage(it.id)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "데이터 저장 실패...", Toast.LENGTH_LONG).show()
                        Log.d("ListFragment", "데이터 저장 실패..")
                    }

            }
        }

        /*
        // Setup the RecyclerView
        val adapter = MyAdapter(datas)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
         */
        binding.mainFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                listener?.onFabClick()
            } else {
                Toast.makeText(requireContext(), "인증을 먼저 진행해주세요!", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    } // onCreateView()

    private fun uploadImage(docId : String) {
        val imageRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        Log.d("ListFragment", "uri값: $uri")
        val uploadTask = uri?.let { imageRef.putFile(it) }
        uploadTask?.addOnSuccessListener {
            Toast.makeText(requireContext(), "사진 업로드 성공!", Toast.LENGTH_LONG).show()
        }
        uploadTask?.addOnFailureListener {
            Toast.makeText(requireContext(), "사진 업로드 실패..", Toast.LENGTH_LONG).show()

        }
    }

    override fun onStart() {
        super.onStart()

        if(MyApplication.checkAuth()){
            MyApplication.db.collection("bus_notifications")
                .get()
                .addOnSuccessListener { result ->
                    val notiList = mutableListOf<NotificationItem>()
                    for(document in result){
                        val noti = document.toObject(NotificationItem::class.java)
                        noti.docId = document.id    // docId는 별도로 처리
                        notiList.add(noti)
                        Log.d("ListFragment", "db에서 가져오기. noti값: $noti, noti.docId값: ${noti.docId}")
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerView.adapter = MyAdapter(requireContext(), notiList)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "서버 데이터 획득 실패", Toast.LENGTH_LONG).show()
                }
        }
    }

    private var listener: OnFabClickListener? = null

    interface OnFabClickListener {
        fun onFabClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFabClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFabClickListener~~!")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
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