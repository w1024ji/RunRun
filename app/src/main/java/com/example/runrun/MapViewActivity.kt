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

// 지도를 표시하고, 마커를 추가하며, 해당 마커를 클릭할 때 다른 화면으로 이동하는 역할을 수행하는 액티비티.
class MapViewActivity : AppCompatActivity(), OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private var mMapView: MapView? = null
    private lateinit var matchingDataList: MutableList<Map<String, Any>>

    // 액티비티 초기화 및 지도 설정.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MapViewActivity", "onCreate() 실행")

        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
        setContentView(R.layout.google_map)
        val matchingDataListJson = intent.getStringExtra("matchingDataListJson")
        matchingDataList = Gson().fromJson(matchingDataListJson, object : TypeToken<MutableList<Map<String, Any>>>() {}.type)
        setupMapView(savedInstanceState)
    }

    // 지도 뷰 설정 및 초기화.
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

    // 일반적인 라이프사이클 이벤트 처리.
    private fun handleCommonLifecycleEvents(tag: String, action: () -> Unit) {
        mMapView?.let { mapView ->
            Log.d(tag, "handleCommonLifecycleEvents() 실행")
            action.invoke()
            mapView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        handleCommonLifecycleEvents("onResume") {
            Log.d("MapViewActivity", "onResume 실행")
        }
    }

    override fun onStart() {
        super.onStart()
        handleCommonLifecycleEvents("onStart") {
            Log.d("MapViewActivity", "onStart 실행")
        }
    }

    override fun onStop() {
        super.onStop()
        handleCommonLifecycleEvents("onStop") {
            Log.d("MapViewActivity", "onStop 실행")
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

    // 지도가 준비되었을 때 호출되는 콜백으로, 마커를 추가하고 지도를 이동시키는 작업 수행.
    override fun onMapReady(map: GoogleMap) {
        Log.d("MapViewActivity", "onMapReady() 실행")
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

        // Using ViewTreeObserver to wait for layout
        mMapView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @Suppress("DEPRECATION")
            override fun onGlobalLayout() {
                mMapView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                // Now that layout has occurred, we can safely move the camera
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            }
        })

        // 마커를 클릭하면
        map.setOnMarkerClickListener { marker ->
            @Suppress("UNCHECKED_CAST")
            val clickedMarkerData = marker.tag as? Map<String, Any>
            if (clickedMarkerData != null) {
                val intent = Intent(this, SetAlarmActivity::class.java)
                val busDataJson = Gson().toJson(clickedMarkerData)
                Log.d("SetAlarmActivity에 넘기는 데이터: ", "busDataJson: $busDataJson") // SetAlarmActivity에 뭘 넘기는 건지
                intent.putExtra("busData", busDataJson)

                // SetAlarmActivity 시작
                startActivity(intent)
            }
            false // Return false to allow the default behavior (info window to open)
        }
    }

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = BuildConfig.GOOGLE_MAP_API
    }

    //  Google Maps SDK 초기화 완료 시 호출되는 콜백.
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