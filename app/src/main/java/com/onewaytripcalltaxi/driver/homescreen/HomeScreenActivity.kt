package com.onewaytripcalltaxi.driver.homescreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.imageview.ShapeableImageView
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.R

class HomeScreenActivity : MainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val profileImageView = findViewById<ShapeableImageView>(R.id.profileImageView)

        Glide.with(this)
            .load("https://fastly.picsum.photos/id/292/200/300.jpg?hmac=zm-TXplXe70N7LGm2HWu9iOPXoBtQvwyhAF2CSj0ccs") // Your drawable resource
            .into(profileImageView)


    }




    override fun setLayout(): Int {
        return R.layout.activity_home_screen
    }

    override fun Initialize() {

    }




}