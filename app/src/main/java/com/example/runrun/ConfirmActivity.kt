package com.example.runrun

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.runrun.databinding.ActivityConfirmBinding


class ConfirmActivity : AppCompatActivity() {
    lateinit var binding : ActivityConfirmBinding
    companion object {
        const val NOTI_NM = "notiNm"
        const val BUS_NM = "busNm"
        const val STA_NM = "staNm"
        const val SELECTED_DAYS = "selectedDays"
        const val WHEN_TO_WHEN = "whenToWhen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SetAlarmActivity에서 보낸 인텐트 데이터 받기
        val notiNm = intent.getStringExtra(NOTI_NM) ?: "Default Notification Name"
        val busNm = intent.getStringExtra(BUS_NM) ?: "Default Bus Name"
        val staNm = intent.getStringExtra(STA_NM) ?: "Default Station Name"
        val selectedDays = intent.getStringExtra(SELECTED_DAYS) ?: "Default Days"
        val whenToWhen = intent.getStringExtra(WHEN_TO_WHEN) ?: "00:00"

        // 사용자에게 컨펌 받기
        initializeViews(notiNm, busNm, staNm, selectedDays, whenToWhen)

        // Next 버튼을 누르면 MainScreenActivity로 recycleView에 쓸 데이터를 들고 간다
        binding.confirmNextButton.setOnClickListener {
            setupAndSendIntent(notiNm, busNm, staNm, selectedDays, whenToWhen)
        }

    } // onCreate()

    private fun initializeViews(notiNm: String, busNm: String, staNm: String, selectedDays: String, whenToWhen: String) {
        binding.confirmNotiNm.text = notiNm
        binding.confirmBus.text = busNm
        binding.confirmStation.text = staNm
        binding.confirmDays.text = selectedDays
        binding.confirmWhenToWhen.text = whenToWhen
    }
    private fun setupAndSendIntent(notiNm: String, busNm: String, staNm: String, selectedDays: String, whenToWhen: String) {
        val intent = Intent(this, MainScreenActivity::class.java)
        intent.putExtra("notiNm", notiNm)
        intent.putExtra("busNm", busNm)
        intent.putExtra("staNm", staNm)
        intent.putExtra("selectedDays", selectedDays)
        intent.putExtra("whenToWhen", whenToWhen)
        startActivity(intent)
    }
}