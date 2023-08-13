package com.example.runrun

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi

class ClientInputActivity : AppCompatActivity() {

    private lateinit var nextButton: Button  // Declare nextButton as a class property

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_input)

        val daySpinner: Spinner = findViewById(R.id.daySpinner)
        val daysOfWeek = resources.getStringArray(R.array.days_of_week)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setupSpinner(daySpinner, dayAdapter)

        val hourSpinner: Spinner = findViewById(R.id.startHourSpinner)
        val hours = resources.getStringArray(R.array.hour)
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setupSpinner(hourSpinner, hourAdapter)

        val minuteSpinner: Spinner = findViewById(R.id.endMinuteSpinner)
        val minutes = resources.getStringArray(R.array.minute)
        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setupSpinner(minuteSpinner, minuteAdapter)

        val hourSpinner2: Spinner = findViewById(R.id.startHourSpinner2)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setupSpinner(hourSpinner2, hourAdapter)

        val minuteSpinner2: Spinner = findViewById(R.id.endMinuteSpinner2)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        setupSpinner(minuteSpinner2, minuteAdapter)

        val nameOfNotify : TextView = findViewById(R.id.nameOfNotify)
        nextButton = findViewById(R.id.nextButton)

        val itemList = intent.getParcelableArrayListExtra("itemList", BusRouteListXmlParser.ItemList::class.java)
        if (itemList != null) {
            for (item in itemList) {
                // Access individual fields: item.rtNm, item.stNm, etc.
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, adapter: ArrayAdapter<String>) {
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = adapter.getItem(position) ?: ""
                // Your logic based on the selected item

                // Enable or disable your button based on the selected item
                nextButton.isEnabled = selectedItem.isNotEmpty()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Disable the button if nothing is selected
                nextButton.isEnabled = false
            }
        }
    }

}