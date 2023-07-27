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

/**
 * This shows how to create a simple activity with a raw MapView and add a marker to it. This
 * requires forwarding all the important lifecycle methods onto MapView.
 */
class RawMapViewDemoActivity : AppCompatActivity(), OnMapReadyCallback,
    OnMapsSdkInitializedCallback {
    private var mMapView: MapView? = null


    var latlng1 : LatLng = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
//        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, onCreate(mapViewBundle))
        setContentView(R.layout.google_map)

        val intent = Intent(this, MainActivity::class.java)
        val lng: Double = intent.getDoubleExtra("lng", 0.0)
        val lat: Double = intent.getDoubleExtra("lat", 0.0)
        latlng1 = LatLng(lat, lng)

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView = findViewById<View>(R.id.map) as MapView
        mMapView!!.onCreate(mapViewBundle)
        println("latlng1 $latlng1")
        mMapView!!.getMapAsync(this)



//        var bounds = LatLngBounds.builder().include(latlng1).build()
//        var cameraP = CameraPosition(latlng1, 13F, 0F, 0F)
//        val options = GoogleMapOptions()
//        var location = CameraUpdateFactory.newCameraPosition(cameraP)
//        var cameraP = CameraPosition.builder()
//        CameraUpdateFactory.newLatLngBounds(bounds, 200)



//        runOnUiThread{
//            options.mapType(GoogleMap.MAP_TYPE_NORMAL)
//                .compassEnabled(true)
//                .rotateGesturesEnabled(true)
//                .tiltGesturesEnabled(true)
//                .camera(cameraP)
//
//            MapView(applicationContext, options)
//        }
//        val cameraP: CameraPosition = CameraPosition.builder().target(latlng1).zoom(13F).build()
//        options.camera(CameraUpdateFactory.newCameraPosition(cameraP))


    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
//        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))
        map.addMarker(MarkerOptions().position(latlng1).title("latlng1"))
    }

    override fun onPause() {
        mMapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = BuildConfig.GOOGLE_MAP_API
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
            else -> {
                println("ONGGGGGGGGGGGGGGGGGGG!!!!")
            }
        }    }
}