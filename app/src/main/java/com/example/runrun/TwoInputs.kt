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


class TwoInputs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_inputs)
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

        val intent = Intent(this, MapViewActivity::class.java)
        intent.putExtra("matchingDataListJson", matchingDataListJson)
        Log.d("RawMapView로 보내는 데이터: ", matchingDataListJson)
        startActivity(intent)
    }

    private fun handleQueryFailure(exception: Exception) {
        Log.d("QueryFailure", "Query failed: $exception")
    }
}
