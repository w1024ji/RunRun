package com.example.runrun

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val stationButton: Button = findViewById(R.id.StNm_button)
        val stationName: EditText = findViewById(R.id.bustStNm_editText)
        stationButton.setOnClickListener {
            stationName.text.toString() // 음.... 일단 여기서 멈추고
        }
    }

}