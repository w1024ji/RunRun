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

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
class RawMapViewDemoActivity : AppCompatActivity(), OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private var mMapView: MapView? = null
    private lateinit var matchingDataList: MutableList<Map<String, Any>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
        setContentView(R.layout.google_map)

        val matchingDataListJson = intent.getStringExtra("matchingDataListJson")
        matchingDataList = Gson().fromJson(matchingDataListJson, object : TypeToken<MutableList<Map<String, Any>>>() {}.type)

        setupMapView(savedInstanceState)
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        var mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            savedInstanceState?.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView = findViewById(R.id.map)
        mMapView?.onCreate(mapViewBundle)
        mMapView?.getMapAsync(this)
    }

    private fun handleCommonLifecycleEvents(tag: String, action: () -> Unit) {
        mMapView?.let { mapView ->
            Log.d(tag, "Executing common lifecycle event actions.")
            action.invoke()
            mapView.onResume()
        }
    }


    override fun onResume() {
        super.onResume()
        handleCommonLifecycleEvents("onResume", {
            Log.d("onResume", "Performing onResume specific actions.")
        })
    }

    override fun onStart() {
        super.onStart()
        handleCommonLifecycleEvents("onStart", {
            Log.d("onStart", "Performing onStart specific actions.")
        })
    }

    override fun onStop() {
        super.onStop()
        handleCommonLifecycleEvents("onStop", {
            Log.d("onStop", "Performing onStop specific actions.")
        })
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        val boundsBuilder = LatLngBounds.Builder() // Create a bounds builder to include all markers

        for (data in matchingDataList) {
            val latitude = data["Y좌표"].toString().toDoubleOrNull() ?: 0.0
            val longitude = data["X좌표"].toString().toDoubleOrNull() ?: 0.0
            val location = LatLng(latitude, longitude)
            Log.d("MarkerDebug", "Adding marker at $longitude, $latitude")

            map.addMarker(MarkerOptions().position(location))

            boundsBuilder.include(location) // Include each marker's location in the bounds
        }

        val bounds = boundsBuilder.build()
        val padding = resources.getDimensionPixelSize(R.dimen.map_padding) // Adjust padding as needed
        Log.d("map_padding 값 : ", "$padding")

        // Set the camera to the bounds and apply padding for a good visual result
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 48))
    }

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = BuildConfig.GOOGLE_MAP_API
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
            else -> {
                Log.d("onMapsSdkInitialized", "when - else 발생")
            }
        }
    }
}