package com.example.runrun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class ClientInputActivity : AppCompatActivity() {private lateinit var daySpinner: Spinner
    private lateinit var adapter: ArrayAdapter<String>
    private val daysOfWeek: Array<String> by lazy {
        resources.getStringArray(R.array.days_of_week)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_input)

        val nextButton : Button = findViewById(R.id.nextButton)
        daySpinner = findViewById(R.id.daySpinner)
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = adapter

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedDay = daysOfWeek[position]
                // You can use the 'selectedDay' variable here or elsewhere in your activity

                // Update the Spinner selection to the chosen day
                adapter.remove("None")
                adapter.insert(selectedDay, 0)
                daySpinner.setSelection(0)

                // Enable the button since something is selected
                nextButton.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Disable the button since nothing is selected
                nextButton.isEnabled = false
            }
        }

        // Set the initial selection to "None"
        val initialSelection = "None"
        val initialPosition = daysOfWeek.indexOf(initialSelection)
        if (initialPosition >= 0) {
            daySpinner.setSelection(initialPosition)
        }
    }
}