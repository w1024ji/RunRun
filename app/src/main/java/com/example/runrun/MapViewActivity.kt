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

import BusParcelable
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.util.ArrayList

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */

// 지도를 표시하고, 마커를 추가하며, 해당 마커를 클릭할 때 다른 화면으로 이동하는 역할을 수행하는 액티비티.
class MapViewActivity : AppCompatActivity(), OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private var mMapView: MapView? = null

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = BuildConfig.GOOGLE_API
        var busDataList : ArrayList<BusParcelable>? = null
    }

    // 액티비티 초기화 및 지도 설정.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MapViewActivity", "onCreate() 실행")

        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
        setContentView(R.layout.google_map)

        busDataList = intent.parcelableArrayList<BusParcelable>("busDataList")
        Log.d("MapViewActivity", "인텐트로 받은 busDataList: $busDataList")
        setupMapView(savedInstanceState)
    }

    inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<BusParcelable>? = when {
        SDK_INT >= 33 -> getParcelableArrayListExtra(key, BusParcelable::class.java)
        else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
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

        if (busDataList != null) {
            // ArrayList<BusParcelable>?인 busParcel에서 두개 이상의 마커를 꺼내 지도에 배치
            // val busData: BusParcelable
            for (busData in busDataList!!) {
                val latitude = busData.yCoordinate.toDoubleOrNull() ?: 0.0
                val longitude = busData.xCoordinate.toDoubleOrNull() ?: 0.0
                val location = LatLng(latitude, longitude)
                Log.d("onMapReady()", "location 값: $location")
                val marker = map.addMarker(MarkerOptions().position(location))
                marker?.tag = busData
                boundsBuilder.include(location)
            }
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

        map.setOnMarkerClickListener { marker ->
            val busData = marker.tag as? BusParcelable
            if (busData != null) {
                val intent = Intent(this, SetAlarmActivity::class.java)
                intent.putExtra("busData", busData)
                Log.d("SetAlarmActivity에 넘기는 데이터: ", "busData: $busData")
                startActivity(intent)
            }
            false // Return false to allow the default behavior (info window to open)
        }
    }

    //  Google Maps SDK 초기화 완료 시 호출되는 콜백.
    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "최신 버전의 renderer가 쓰이고 있음")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "낡은 버전의 renderer가 쓰이고 있음")
            else -> {
                Log.d("onMapsSdkInitialized", "when - else 발생")
            }
        }
    }
}