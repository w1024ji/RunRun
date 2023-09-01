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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        Log.d("matchingDataList 값 : ", "$matchingDataListJson") // 잘 도착함

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
        handleCommonLifecycleEvents("onResume") {
            Log.d("onResume", "Performing onResume specific actions.")
        }
    }

    override fun onStart() {
        super.onStart()
        handleCommonLifecycleEvents("onStart") {
            Log.d("onStart", "Performing onStart specific actions.")
        }
    }

    override fun onStop() {
        super.onStop()
        handleCommonLifecycleEvents("onStop") {
            Log.d("onStop", "Performing onStop specific actions.")
        }
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

            val marker = map.addMarker(MarkerOptions().position(location))
            marker?.tag = data

            boundsBuilder.include(location)
        }

        val bounds = boundsBuilder.build()
        val padding = resources.getDimensionPixelSize(R.dimen.map_padding)
        Log.d("map_padding 값 : ", "$padding")

        // Using ViewTreeObserver to wait for layout
        mMapView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @Suppress("DEPRECATION")
            override fun onGlobalLayout() {
                mMapView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                // Now that layout has occurred, we can safely move the camera
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            }
        })

        map.setOnMarkerClickListener { marker ->

            @Suppress("UNCHECKED_CAST")
            val clickedMarkerData = marker.tag as? Map<String, Any>
            Log.d("MarkerData", "Type of clickedMarkerData: ${clickedMarkerData?.javaClass?.simpleName}") // LinkedTreeMap

            if (clickedMarkerData != null) {
                val ordId = clickedMarkerData["순번"]?.toString() ?: ""
                val routeId = clickedMarkerData["ROUTE_ID"]?.toString() ?: ""
                val nodeId = clickedMarkerData["NODE_ID"]?.toString() ?: ""

                val intent = Intent(this, MyForegroundService::class.java)

                intent.putExtra("ordId", ordId)
                intent.putExtra("routeId", routeId)
                intent.putExtra("nodeId", nodeId)
                ContextCompat.startForegroundService(this, intent)
            }
            false // Return false to allow the default behavior (info window to open)
        }
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