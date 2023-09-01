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
                R.style.SpinnerTimePickerDialogTheme,
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
                R.style.SpinnerTimePickerDialogTheme,
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
                        R.id.sundayChip -> selectedDays.add(Calendar.SUNDAY) // 1
                        R.id.mondayChip -> selectedDays.add(Calendar.MONDAY) // 2
                        R.id.tuesdayChip -> selectedDays.add(Calendar.TUESDAY) // 3
                        R.id.wednesdayChip -> selectedDays.add(Calendar.WEDNESDAY) // 4
                        R.id.thursdayChip -> selectedDays.add(Calendar.THURSDAY) // 5
                        R.id.fridayChip -> selectedDays.add(Calendar.FRIDAY) // 6
                        R.id.saturdayChip -> selectedDays.add(Calendar.SATURDAY) // 7
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

        val startIntent = Intent(this, AlarmReceiver::class.java)
        val startPendingIntent = PendingIntent.getBroadcast(this, 88, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(this, 99, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        for (day in days) {
            Log.d("SetAlarmActivity", "Scheduling alarm for day: $day")

            val now = Calendar.getInstance()

            val startCalendar: Calendar = now.clone() as Calendar
            startCalendar.set(Calendar.DAY_OF_WEEK, day)
            startCalendar.set(Calendar.HOUR_OF_DAY, startHour)
            startCalendar.set(Calendar.MINUTE, startMinute)
            startCalendar.set(Calendar.SECOND, 0)
            startCalendar.set(Calendar.MILLISECOND, 0)

            // Ensure we're setting the alarm for the next occurrence of this time.
            if (startCalendar.before(now)) {
                startCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            val endCalendar: Calendar = now.clone() as Calendar
            endCalendar.set(Calendar.DAY_OF_WEEK, day)
            endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
            endCalendar.set(Calendar.MINUTE, endMinute)
            endCalendar.set(Calendar.SECOND, 0)
            endCalendar.set(Calendar.MILLISECOND, 0)

            // Same here, make sure the endCalendar is for the next occurrence
            if (endCalendar.before(now)) {
                endCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            // Log the Unix timestamps
            Log.d("SetAlarmActivity", "Start time in millis: ${startCalendar.timeInMillis}")
            Log.d("SetAlarmActivity", "End time in millis: ${endCalendar.timeInMillis}")

            // schedules a repeating alarm using Android's AlarmManager
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
