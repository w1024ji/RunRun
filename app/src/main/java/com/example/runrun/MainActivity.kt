package com.example.runrun

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val xmlButton: Button = findViewById(R.id.xml_button)
        val intent = Intent(this, NetworkActivity::class.java)
        intent.putExtra("startOrStop", "start")
        startActivity(intent)
        // startActivity()가 인텐트를 시스템에 전달한다.
        // 이 함수의 매개변수에는 시스템에 실행을 요청할 컴포넌트의 정보가 담긴 Intent 객체를 전달한다.

        println("this is contained in my second commit")
        today is a good day
    }
}