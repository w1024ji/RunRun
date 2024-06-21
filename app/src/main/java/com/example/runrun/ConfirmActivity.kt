package com.example.runrun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.runrun.databinding.ActivityConfirmBinding

class ConfirmActivity : AppCompatActivity() {
    lateinit var binding : ActivityConfirmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SetAlarmActivity에서 보낸 인텐트 데이터 받기
        val notiNm = intent.getStringExtra("notiNm") ?: "Default Notification Name"
        val busNm = intent.getStringExtra("busNm") ?: "Default Bus Name"
        val staNm = intent.getStringExtra("staNm") ?: "Default Station Name"
        val selectedDays = intent.getStringExtra("selectedDays") ?: "Default Days"
        val whenToWhen = intent.getStringExtra("whenToWhen") ?: "00:00"
        val uri = intent.getStringExtra("uri") ?: "Default uri"
        Log.d("ConfirmActivity", "인텐트로 받은 notiNm값: $notiNm, selectedDays값: $selectedDays")
        Log.d("ConfirmActivity", "인텐트로 받은 uri값: $uri")

        // 사용자에게 컨펌 받기
        initializeViews(notiNm, busNm, staNm, selectedDays, whenToWhen)

        // Next 버튼을 누르면 MainScreenActivity로 recycleView에 쓸 데이터를 들고 간다
        binding.confirmNextButton.setOnClickListener {
            setupAndSendIntent(notiNm, busNm, staNm, selectedDays, whenToWhen, uri)
        }

    } // onCreate()

    private fun initializeViews(notiNm: String, busNm: String, staNm: String, selectedDays: String, whenToWhen: String) {
        binding.confirmNotiNm.text = notiNm
        binding.confirmBus.text = busNm
        binding.confirmStation.text = staNm
        binding.confirmDays.text = selectedDays
        binding.confirmWhenToWhen.text = whenToWhen
    }
    private fun setupAndSendIntent(notiNm: String, busNm: String, staNm: String, selectedDays: String, whenToWhen: String, uri:String) {
        val intent = Intent(this, MainScreenActivity::class.java)
        intent.putExtra("notiNm", notiNm)
        intent.putExtra("busNm", busNm)
        intent.putExtra("staNm", staNm)
        intent.putExtra("selectedDays", selectedDays)
        intent.putExtra("whenToWhen", whenToWhen)
        intent.putExtra("uri", uri)
        Log.d("ConfirmActivity", "setupAndSendIntent()의 uri값: $uri")

        startActivity(intent)
    }
}