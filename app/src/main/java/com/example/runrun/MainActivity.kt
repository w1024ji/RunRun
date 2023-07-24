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

        val applyButton: Button = findViewById(R.id.applyButton)
        val busName: EditText = findViewById(R.id.enterBusName)
        val stationName: EditText = findViewById(R.id.enterStation)
        applyButton.setOnClickListener {

        }
    }

}