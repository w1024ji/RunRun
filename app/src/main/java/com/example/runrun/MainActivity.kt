package com.example.runrun

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


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
//        val busName: EditText = findViewById(R.id.enterBusName)
//        val stationName: EditText = findViewById(R.id.enterStation)
//        applyButton.setOnClickListener {
//            // 사용자의 입력을 잘 받는지 테스트
//            var busNm : String = busName.text.toString()
//            var stNm : String = stationName.text.toString()
//
//            val intent = Intent(this, RawMapViewDemoActivity::class.java)
//            intent.putExtra("lng",  127.0494952)
//            intent.putExtra("lat", 37.66042516)
//            startActivity(intent)

        val startAutocomplete =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult -> try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent == null) Log.e(
                        "AutocompleteError-Null",
                        "Error starting Autocomplete activity -01"
                    )
                    if (intent != null) {
                        val place = Autocomplete.getPlaceFromIntent(intent)
                        println("Place 값: ${place.name}, ${place.id}")
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    println("User canceled autocomplete")
                }
            } catch (e: Exception){
                Log.e("AutocompleteError", "Error starting Autocomplete activity", e)
            }
            }
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GOOGLE_MAP_API, Locale.US);
        }

        applyButton.setOnClickListener {
            try {
                // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this)
                startAutocomplete.launch(intent)
            } catch (e: Exception) {
                Log.e("AutocompleteError", "Error starting Autocomplete activity", e)
            }
        }
    }

}