package com.example.runrun

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.runrun.databinding.ClientInputBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*
import kotlin.collections.ArrayList

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

        nextButton.setOnClickListener {
            val selectedDays = getSelectedDays()
            val startHour = binding.startHourSpinner.selectedItem.toString().toInt()
            val startMinute = binding.endMinuteSpinner.selectedItem.toString().toInt()
            val endHour = binding.startHourSpinner2.selectedItem.toString().toInt()
            val endMinute = binding.endMinuteSpinner2.selectedItem.toString().toInt()

            val dayMapping = mapOf(
                "Sunday" to Calendar.SUNDAY,
                "Monday" to Calendar.MONDAY,
                "Tuesday" to Calendar.TUESDAY,
                "Wednesday" to Calendar.WEDNESDAY,
                "Thursday" to Calendar.THURSDAY,
                "Friday" to Calendar.FRIDAY,
                "Saturday" to Calendar.SATURDAY
            )

            selectedDays.forEach { day ->
                dayMapping[day]?.let {
                    setAlarm(it, startHour, startMinute)
                    // You can also set the ending alarm if needed
                    setAlarm(it, endHour, endMinute)
                }
            }
        }

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
    private fun getSelectedDays(): List<String> {
        val selectedDays = mutableListOf<String>()
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                selectedDays.add(chip.text.toString())
            }
        }
        return selectedDays
    }
    private fun setAlarm(dayOfWeek: Int, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

//        val alarmIntent = Intent(this, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)

//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


}