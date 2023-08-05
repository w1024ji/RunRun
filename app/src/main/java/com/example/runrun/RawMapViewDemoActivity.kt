// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.runrun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.concurrent.thread

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
class RawMapViewDemoActivity : AppCompatActivity(), OnMapReadyCallback,
    OnMapsSdkInitializedCallback {
    private var mMapView: MapView? = null
    // Declare matchingDataList as a class-level property
    private lateinit var matchingDataList: MutableList<Map<String, Any>>

    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate() 함수 작동")
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
        setContentView(R.layout.google_map)

        // Retrieve matchingDataList JSON from intent extras
        val matchingDataListJson = intent.getStringExtra("matchingDataListJson")
        // Convert JSON to MutableList<Map<String, Any>>
        matchingDataList = Gson().fromJson(matchingDataListJson, object : TypeToken<MutableList<Map<String, Any>>>() {}.type)

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView = findViewById<View>(R.id.map) as MapView
        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)


    }

    public override fun onSaveInstanceState(outState: Bundle) {
        println("onSaveInstanceState() 함수 작동")
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        println("onResume() 함수 작동")
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onStart() {
        println("onStart() 함수 작동")
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onStop() {
        println("onStop()함수 작동")
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        println("onMapReady()함수 작동")
        for (data in matchingDataList) {
            val latitude = data["Y좌표"].toString().toDouble()
            val longitude = data["X좌표"].toString().toDouble()

            val location = LatLng(latitude, longitude)
            Log.d("MarkerDebug", "Adding marker at $longitude, $latitude")

            map.addMarker(MarkerOptions().position(location))
        }
    }

    override fun onPause() {
        println("onPause()함수 작동")
        mMapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        println("onDestroy()함수 작동")
        mMapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        println("onLowMemory()함수 작동")
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = BuildConfig.GOOGLE_MAP_API
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        println("onMapsSdkInitialized()함수 작동")
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
            else -> {
                Log.d("onMapsSdkInitialized", "when - else 발생")
            }
        }
    }
}