package com.example.runrun

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val xmlButton: Button = findViewById(R.id.xml_button)
        xmlButton.setOnClickListener{

            val intent = Intent(this, NetworkActivity::class.java)
            intent.putExtra("startOrStop", "start")
            startActivity(intent)
            //finish()

        }

        val applyButton: Button = findViewById(R.id.applyButton)
        val busName: EditText = findViewById(R.id.enterBusName)
        val stationName: EditText = findViewById(R.id.enterStation)
        applyButton.setOnClickListener {
            // 사용자의 입력을 잘 받는지 테스트
            var busNm : String = busName.text.toString()
            var stNm : String = stationName.text.toString()

            val intent = Intent(this, RawMapViewDemoActivity::class.java)
            intent.putExtra("lng",  127.0494952)
            intent.putExtra("lat", 37.66042516)
            startActivity(intent)

        }
    }

}