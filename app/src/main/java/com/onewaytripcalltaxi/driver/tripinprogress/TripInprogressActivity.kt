package com.onewaytripcalltaxi.driver.tripinprogress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.interfaces.LocalDistanceInterface

class TripInprogressActivity : AppCompatActivity(), OnMapReadyCallback, LocalDistanceInterface,
    OnCameraMoveStartedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_trip_inprogress)

    }

    override fun onMapReady(p0: GoogleMap) {

    }

    override fun haversineResult(success: Boolean?) {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }
}