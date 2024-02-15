package com.example.runrun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AfterSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_setting)

        val returnTo : Button = findViewById(R.id.returnTo)

    }
}