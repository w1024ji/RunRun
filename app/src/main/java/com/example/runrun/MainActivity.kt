package com.example.runrun

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val xmlButton: Button = findViewById(R.id.xml_button)
        xmlButton.setOnClickListener{
            // 스레드 어떻게 만들어서 사용해야 하는 거지
            thread(start = true) {

                val intent = Intent(this, NetworkActivity::class.java)
                intent.putExtra("startOrStop", "start")
                startActivity(intent)
                //finish()
            }
        }
    }

}