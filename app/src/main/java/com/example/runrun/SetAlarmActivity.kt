package com.example.runrun


import BusParcelable
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    lateinit var busNm : String
    lateinit var staNm : String

    lateinit var busData : BusParcelable
    lateinit var selectedDays : MutableList<Int>

    // 이미지 업로드
    lateinit var uri : Uri

    inline fun <reified BusParcelable : Parcelable> Intent.parcelable(key: String): BusParcelable? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, BusParcelable::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? BusParcelable
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
        // 이미지 업로드
        val uploadButton : Button = findViewById(R.id.uploadButton)
        var addImageView : ImageView = findViewById(R.id.addImageView)

        // MapViewActivity에서 전달된 데이터를 받아옴
        busData = intent.parcelable("busData")!!

        busNm = busData.routeName
        staNm = busData.stationName

        routeNm.text = busData.routeName
        stationNm.text = busData.stationName
        ordId = busData.sequence
        routeId = busData.routeId
        nodeId = busData.nodeId
        Log.d("SetAlarmActivity", "ordId값: $ordId, routeId값: $routeId, nodeId값: $nodeId")

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

        // 이미지 업로드 하기
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == android.app.Activity.RESULT_OK){
                addImageView.visibility = View.VISIBLE // 이미지 뷰 다시 보이게
                Glide
                    .with(applicationContext)
                    .load(it.data?.data)
                    .override(200, 150) // 사이즈
                    .into(addImageView)
                uri = it.data?.data!!   // uri는 절대 null이어서는 안돼
                Log.d("SetAlarmActivity", "uri값: $uri")
            }
        }
        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }


        setAlarmButton.setOnClickListener {
            Log.d("SetAlarmActivity", "Set Alarm 버튼 클릭")

            selectedDays = mutableListOf<Int>()
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
            Log.d("SetAlarmActivity", "selectedDays: $selectedDays")
            scheduleAlarm(startHour, startMinute, endHour, endMinute, selectedDays)
            goToConfirmActivity() // 알람 세팅 끝났으면 컨펌 화면으로 넘기기
        }
    } // onCreate()

    // 알람 세팅 끝났으면 ConfimActivity로 화면 전환
    private fun goToConfirmActivity(){
        var days: MutableList<String> = mutableListOf()

        for (i in 0 until selectedDays.size) {
            when (selectedDays[i]) {
                1 -> days.add("sunday")
                2 -> days.add("monday")
                3 -> days.add("tuesday")
                4 -> days.add("wednesday")
                5 -> days.add("thursday")
                6 -> days.add("friday")
                7 -> days.add("saturday")
            }
        }
        val confirmIntent = Intent(this, ConfirmActivity::class.java)
        confirmIntent.putExtra("notiNm", "알람 이름: ${notiNm.text}") // 알람 이름
        confirmIntent.putExtra("busNm", "버스 이름: $busNm") // 버스
        confirmIntent.putExtra("staNm", "정류장 이름: $staNm") // 정류장
        confirmIntent.putExtra("selectedDays", "선택한 요일: ${days}") // 요일
        val whenToWhen = "$startHour:$startMinute~$endHour:$endMinute"
        confirmIntent.putExtra("whenToWhen", whenToWhen)
        Log.d("SetAlarmActivity", "ConfirmActivity로 보내는 인텐트 속 uri값: $uri") // content://com.google.android.apps.photos.cont...
        confirmIntent.putExtra("uri", uri.toString()) // 이미지 uri 보내기 >>toString()꼭 해야함<<

        startActivity(confirmIntent)
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
        startIntent.putExtra("notiNm", notiNm.text.toString()) // toString() 꼭 해야 함
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
