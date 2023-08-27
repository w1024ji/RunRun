package com.example.runrun

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*


class SetAlarmActivity : AppCompatActivity() {

    private var startHour = 0
    private var startMinute = 0
    private var endHour = 0
    private var endMinute = 0
    var routeName: String? = null
    var stationName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)
        Log.d("SetAlarmActivity", "Activity created")

        val startTimeTextView: TextView = findViewById(R.id.startTimeTextView)
        val endTimeTextView: TextView = findViewById(R.id.endTimeTextView)
        val dayChipGroup: ChipGroup = findViewById(R.id.dayChipGroup)
        val setAlarmButton: Button = findViewById(R.id.setAlarmButton)
        val route01 : TextView = findViewById(R.id.bus01)
        val station01 : TextView = findViewById(R.id.station01)

        // deprecated이지만 getParcelableArrayListExtra(string, Class) 쓰려면 API 33이상만 돼서 일단 보류.
        val itemList: ArrayList<BusRouteListXmlParser.ItemList>? = intent.getParcelableArrayListExtra("itemList")

        itemList?.forEach { item ->
            routeName = item.rtNm
            stationName = item.stNm
        }
        route01.text = routeName
        station01.text = stationName

        startTimeTextView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                R.style.SpinnerTimePickerDialogTheme,  // Apply the custom style
                { _, hourOfDay, minute ->
                    startHour = hourOfDay
                    startMinute = minute
                    startTimeTextView.text = String.format("%02d:%02d", hourOfDay, minute)
                },
                startHour,
                startMinute,
                true
            )
            timePickerDialog.show()
        }

        endTimeTextView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                R.style.SpinnerTimePickerDialogTheme,  // Apply the custom style
                { _, hourOfDay, minute ->
                    endHour = hourOfDay
                    endMinute = minute
                    endTimeTextView.text = String.format("%02d:%02d", hourOfDay, minute)
                },
                endHour,
                endMinute,
                true
            )
            timePickerDialog.show()
        }

        setAlarmButton.setOnClickListener {
            Log.d("SetAlarmActivity", "Set Alarm button clicked")
            val selectedDays = mutableListOf<Int>()
            for (i in 0 until dayChipGroup.childCount) {
                val chip = dayChipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    when (chip.id) {
                        R.id.mondayChip -> selectedDays.add(Calendar.MONDAY)
                        R.id.tuesdayChip -> selectedDays.add(Calendar.TUESDAY)
                        R.id.wednesdayChip -> selectedDays.add(Calendar.WEDNESDAY)
                        R.id.thursdayChip -> selectedDays.add(Calendar.THURSDAY)
                        R.id.fridayChip -> selectedDays.add(Calendar.FRIDAY)
                        R.id.saturdayChip -> selectedDays.add(Calendar.SATURDAY)
                        R.id.sundayChip -> selectedDays.add(Calendar.SUNDAY)
                    }
                }
            }

            // Schedule the alarm based on the times and days selected
            scheduleAlarm(startHour, startMinute, endHour, endMinute, selectedDays)
        }
    }

    private fun scheduleAlarm(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int, days: List<Int>) {
        Log.d("SetAlarmActivity", "Scheduling alarm")

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent for starting the alarm
        val startIntent = Intent(this, AlarmReceiver::class.java)
        val startPendingIntent = PendingIntent.getBroadcast(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent for stopping the alarm
        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the alarm to start and end at the selected times
        val startCalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
        }

        val endCalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
        }

        for (day in days) {
            Log.d("SetAlarmActivity", "Scheduling alarm for day: $day")

            startCalendar.set(Calendar.DAY_OF_WEEK, day)
            endCalendar.set(Calendar.DAY_OF_WEEK, day)
            Log.d("scheduleAlarm", "startCalendar : $startCalendar, endCalendar : $endCalendar")

            // Schedule the alarms
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startCalendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                startPendingIntent
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                endCalendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                stopPendingIntent
            )
        }
    }
}
