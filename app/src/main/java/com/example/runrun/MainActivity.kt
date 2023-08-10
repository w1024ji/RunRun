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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNetworkButton()
        setupApplyButton()
    }

    private fun setupNetworkButton() {
        val networkButton: Button = findViewById(R.id.xml_button)
        networkButton.setOnClickListener {
            val intent = Intent(this, NetworkActivity::class.java)
            intent.putExtra("startOrStop", "start")
            startActivity(intent)
        }
    }

    private fun setupApplyButton() {
        val applyButton: Button = findViewById(R.id.applyButton)
        val stationName: EditText = findViewById(R.id.enterStation)
        val busName: EditText = findViewById(R.id.enterBusName)

        applyButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val busInfoCollection = db.collection("busInfo")
            val stationNameInput = stationName.text.toString()
            val busNameInput = busName.text.toString()

            val query = busInfoCollection
                .whereEqualTo("정류소명", stationNameInput)
                .whereEqualTo("노선명", busNameInput)

            query.get()
                .addOnSuccessListener { querySnapshot ->
                    handleQuerySuccess(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    handleQueryFailure(exception)
                }
        }
    }

    private fun handleQuerySuccess(documents: List<DocumentSnapshot>) {
        val matchingDataList = documents.mapNotNull { it.data }
        val matchingDataListJson = Gson().toJson(matchingDataList)

        val intent = Intent(this, RawMapViewDemoActivity::class.java)
        intent.putExtra("matchingDataListJson", matchingDataListJson)
        startActivity(intent)
    }

    private fun handleQueryFailure(exception: Exception) {
        Log.d("QueryFailure", "Query failed: $exception")
    }
}
