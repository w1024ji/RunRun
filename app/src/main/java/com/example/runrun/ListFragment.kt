package com.example.runrun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runrun.databinding.FragmentListBinding

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
    // binding
    lateinit var binding : FragmentListBinding
    private var datas: MutableList<NotificationItem> = mutableListOf()

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

        // Retrieve arguments from the bundle
        Log.d("ListFragment", "arguments값: $arguments")
        // Retrieve arguments from the bundle
        arguments?.let {
            val notiNm = it.getString("notiNm")
            val busNm = it.getString("busNm")
            val staNm = it.getString("staNm")
            val selectedDays = it.getString("selectedDays")
            val whenToWhen = it.getString("whenToWhen")

            if (notiNm != null && busNm != null && staNm != null && selectedDays != null && whenToWhen != null) {
                val notificationItem = NotificationItem(notiNm, busNm, staNm, selectedDays, whenToWhen)
                datas.add(notificationItem)
            }
        }

        /* Setup the RecyclerView */
        // Setup the RecyclerView
        val adapter = MyAdapter(datas)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        /*
        // 플로팅 액션 버튼이 눌려지면 AddFragment로 넘어가
        binding.mainFab.setOnClickListener {
            // 만약 사용자가 로그인 상태라면..
            if(MyApplication.checkAuth()){
                TODO()
                val intent = Intent(requireContext(), AddFragment::class.java)
                Log.d("ListFragment", "플로팅 버튼 누름. 인텐트 값: $intent")
                startActivity(intent)
            }
            else {
                Toast.makeText(requireContext(), "인증을 먼저 진행해주세요!", Toast.LENGTH_LONG).show()
            }
        }

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