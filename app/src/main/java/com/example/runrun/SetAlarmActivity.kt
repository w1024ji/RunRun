package com.example.runrun


import BusParcelable
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.runrun.MapViewActivity.Companion.busParcel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*

// 사용자가 알림을 설정하는 화면으로, 알림 시작 및 종료 시간, 반복 일자를 설정하고 알림을 스케줄링하는 역할을 수행하는 액티비티.
class SetAlarmActivity : AppCompatActivity() {

    private var startHour = 0
    private var startMinute = 0
    private var endHour = 0
    private var endMinute = 0
    private lateinit var ordId : String
    private lateinit var routeId : String
    private lateinit var nodeId : String
    private lateinit var notiNm : TextView

    lateinit var busParcel : BusParcelable

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

    // 알림 설정 화면 초기화 및 UI 구성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)
        Log.d("SetAlarmActivity", "onCreate() 실행")

        val startTimeTextView: TextView = findViewById(R.id.startTimeTextView)
        val endTimeTextView: TextView = findViewById(R.id.endTimeTextView)
        val dayChipGroup: ChipGroup = findViewById(R.id.dayChipGroup)
        val setAlarmButton: Button = findViewById(R.id.setAlarmButton)
        val routeNm : TextView = findViewById(R.id.bus01)
        val stationNm : TextView = findViewById(R.id.station01)
        notiNm = findViewById(R.id.notificationName)

        // MapViewActivity에서 전달된 데이터를 받아옴
//        val busDataJson = intent.getStringExtra("busDataJson").toString()
////        Log.d("SetAlarmActivity", "인텐트 받은 값: $busDataJson")
//        val busData : Map<String, Any> = Gson().fromJson(busDataJson, object : TypeToken<Map<String, Any>>() {}.type)

//        val busData = intent.serializable("busData") as? MainActivity.BusData
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            busParcel = intent.getParcelableExtra("busData", BusParcelable::class.java)!!
        }
        routeNm.text = busParcel.routeName
        stationNm.text = busParcel.stationName
        ordId = busParcel.sequence
        routeId = busParcel.routeId
        nodeId = busParcel.nodeId
        Log.d("SetAlarmActivity", "ordId값: $ordId, routeId값: $routeId, nodeId값: $nodeId")


//        routeNm.text = busData["노선명"]?.toString() ?: ""
//        stationNm.text = busData["정류소명"]?.toString() ?: ""
//        ordId = busData["순번"]?.toString() ?: ""
//        routeId = busData["ROUTE_ID"]?.toString() ?: ""
//        nodeId = busData["NODE_ID"]?.toString() ?: ""


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
            Log.d("SetAlarmActivity", "Set Alarm 버튼 클릭")

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
            scheduleAlarm(startHour, startMinute, endHour, endMinute, selectedDays)
            goToafterSet()
        }
    }

    private fun goToafterSet(){
        val afterSetIntent = Intent(this, AfterSetting::class.java)
        startActivity(afterSetIntent)
    }

    // 알림을 스케줄링하는 메서드
    private fun scheduleAlarm(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int, days: List<Int>) {
        Log.d("SetAlarmActivity", "scheduleAlarm() 시작")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // 시작 알람과 정지 알람에 사용될 PendingIntent 설정
        val startIntent = Intent(this, AlarmReceiver::class.java)
        startIntent.action = "START_FOREGROUND_SERVICE"
        startIntent.putExtra("ordId", ordId)
        startIntent.putExtra("routeId", routeId)
        startIntent.putExtra("nodeId", nodeId)
        startIntent.putExtra("notiNm", notiNm.text.toString())
//        Log.d("SetAlarmActivity", "notiNm 값: ${notiNm.text.toString()}")
        val startPendingIntent = PendingIntent.getBroadcast(this, 88, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        stopIntent.action = "STOP_FOREGROUND_SERVICE"
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

            // 갱신 시간에서 intervalMillis 간격으로 알람 설정
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
