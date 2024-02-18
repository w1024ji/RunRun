package com.example.runrun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.io.Serializable


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupApplyButton()
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

            Log.d("MainActivity", "db값: $db, busInfoCollection값: $busInfoCollection")

            val query = busInfoCollection
                .whereEqualTo("정류소명", stationNameInput)
                .whereEqualTo("노선명", busNameInput)

            Log.d("MainActivity", "query값: $query")

            query.get()
                .addOnSuccessListener { querySnapshot ->
                    handleQuerySuccess(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    handleQueryFailure(exception)
                }
        }
    }

    data class BusData(
        val arsId: String,
        val nodeId: String,
        val routeId: String,
        val xCoordinate: String,
        val yCoordinate: String,
        val routeName: String,
        val sequence: String,
        val stationName: String
    ) : Serializable

    private fun handleQuerySuccess(documents: List<DocumentSnapshot>) {

        val matchingDataList = documents.mapNotNull { it.data }
        val busDataList = matchingDataList.map {
            BusData(
                it["ARS_ID"].toString(),
                it["NODE_ID"].toString(),
                it["ROUTE_ID"].toString(),
                it["X좌표"].toString(),
                it["Y좌표"].toString(),
                it["노선명"].toString(),
                it["순번"].toString(),
                it["정류소명"].toString()
            )
        }

        val intent = Intent(this, MapViewActivity::class.java)
        intent.putExtra("busDataList", busDataList.toTypedArray())
        Log.d("MainActivity", "MapView로 보내는 인텐트: ${busDataList.toTypedArray()}") // [Lcom.example.runrun.MainActivity$BusData;@55d53ae
        startActivity(intent)
    }

    private fun handleQueryFailure(exception: Exception) {
        Log.d("QueryFailure", "Query failed: $exception")
    }
}
