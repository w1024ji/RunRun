package com.example.runrun

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.PlaceTypes.BUS_STATION
import com.google.android.libraries.places.api.model.PlaceTypes.ESTABLISHMENT
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val xmlButton: Button = findViewById(R.id.xml_button)
        xmlButton.setOnClickListener {
            val intent = Intent(this, NetworkActivity::class.java)
            intent.putExtra("startOrStop", "start")
            startActivity(intent)
            //finish()
        }

        val applyButton: Button = findViewById(R.id.applyButton)
        val busName: EditText = findViewById(R.id.enterBusName)
        val stationName: EditText = findViewById(R.id.enterStation)

        applyButton.setOnClickListener {
            // 사용자의 입력을 잘 받는지 테스트
            var busNm: String = busName.text.toString()
            var stNm: String = stationName.text.toString()

            // Firestore에서 stNm이 일치하는 데이터행들 중에서
            // 같은 이름을 가진 정류장들의 x좌표, y좌표를 가져와야 한다 now this is the case.

            // Initialize Firestore
            val db = FirebaseFirestore.getInstance()

            // Reference to the "busInfo" collection
            val busInfoCollection = db.collection("busInfo")

            // User-entered bus stop name
            val enteredBusStopName = "종로2가사거리" // Replace this with the actual input

            // Query to retrieve data sets with matching "정류소명"
            val query = busInfoCollection.whereEqualTo("정류소명", enteredBusStopName)

            // Execute the query
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    val matchingDataList = mutableListOf<Map<String, Any>>()

                    for (documentSnapshot in querySnapshot.documents) {
                        val matchingData = documentSnapshot.data
                        if (matchingData != null) {
                            matchingDataList.add(matchingData)
                        }
                        else{
                            Log.d("addOnSuccessListener : ", "matchingData가 null이다!")
                        }
                    }
                    Log.d("matchingDataList 확인 : ", matchingDataList.toString())

                    // Now you have a list of matching data sets
                    // You can process or display them as needed
                }
                .addOnFailureListener { exception ->
                    Log.d("addOnFailureListener : ", "실패! : $exception")
                }

            // Intent to RawMapViewDemoActivity.kt
//            val intent = Intent(this, RawMapViewDemoActivity::class.java)
//            intent.putExtra("lng", 127.0494952)
//            intent.putExtra("lat", 37.66042516)
//            startActivity(intent)
        }


//        >>>>> Autocomplete으로는 내가 원하는 결과를 얻을 수 없다고 판단, 다른 방법으로 대체 <<<<<
//        val startAutocomplete =
//            registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult()
//            ) { result: ActivityResult -> try {
//                if (result.resultCode == Activity.RESULT_OK) {
//                    val intent = result.data
//                    if (intent == null) Log.e(
//                        "AutocompleteError-Null",
//                        "Error starting Autocomplete activity -01"
//                    )
//                    if (intent != null) {
//                        val place = Autocomplete.getPlaceFromIntent(intent)
//                        println("Place 값: ${place.name}, ${place.id}")
//                        // After getting the selected place's details from the autocomplete,
//                        // you can launch the Google Maps app with an intent that includes
//                        // the place's latitude and longitude.
//                        val location: LatLng? = place.latLng
//                        println("location값 : $location")
//                        if (location != null){
//                            println("place와 location값 : $place, $location")
//                            val intent2 = Intent(Intent.ACTION_VIEW,
//                                Uri.parse("geo:${location.latitude},${location.longitude}?q=${place.name}"))
//                            startActivity(intent2)
//                        }
//                    }
//                } else if (result.resultCode == Activity.RESULT_CANCELED) {
//                    // The user canceled the operation.
//                    println("User canceled autocomplete")
//                }
//            } catch (e: Exception){
//                Log.e("AutocompleteError", "Error starting Autocomplete activity", e)
//            }
//            }
//
//        if (!Places.isInitialized()) {
//            Places.initialize(applicationContext, BuildConfig.GOOGLE_MAP_API, Locale.US);
//        }
//
//        applyButton.setOnClickListener {
//            try {
//                // Set the fields to specify which types of place data to
//                // return after the user has made a selection.
//                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//                val placeTypes = mutableListOf(BUS_STATION)
//                // Start the autocomplete intent.
//                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                    .setTypesFilter(placeTypes)
//                    .build(this)
//                startAutocomplete.launch(intent)
//            } catch (e: Exception) {
//                Log.e("AutocompleteError", "Error starting Autocomplete activity", e)
//            }
//        }
    }
}