package com.example.runrun

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.runrun.databinding.ClientInputBinding

class ClientInputActivity : AppCompatActivity() {

    private lateinit var nextButton: Button
    private lateinit var binding: ClientInputBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ClientInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setupSpinners(binding.daySpinner, R.array.days_of_week)
        setupSpinners(binding.startHourSpinner, R.array.hour)
        setupSpinners(binding.endMinuteSpinner, R.array.minute)
        setupSpinners(binding.startHourSpinner2, R.array.hour)
        setupSpinners(binding.endMinuteSpinner2, R.array.minute)

        nextButton = binding.nextButton

        var routeName: String? = null
        var stationName: String? = null

        // getParcelableArrayListExtra - deprecated -
        // The function is working well, so I'm not gonna change this "for now".
        // Later, I should look for a better way to carry complicated data through Intent.
        val itemList: ArrayList<BusRouteListXmlParser.ItemList>? = intent.getParcelableArrayListExtra("itemList")
        itemList?.forEach { item ->
            routeName = item.rtNm
            stationName = item.stNm
        }

        binding.selectedBus.text = routeName
        binding.selectedStation.text = stationName
        var nameOfNotification = binding.nameOfNotification
    }

    private fun setupSpinners(spinner: Spinner, optionsArrayResId: Int) {
        val options = resources.getStringArray(optionsArrayResId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = adapter.getItem(position) ?: ""
                nextButton.isEnabled = selectedItem.isNotBlank()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                nextButton.isEnabled = false
            }
        }
    }

}