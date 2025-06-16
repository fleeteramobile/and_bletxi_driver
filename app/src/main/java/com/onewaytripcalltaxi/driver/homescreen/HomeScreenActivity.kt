package com.onewaytripcalltaxi.driver.homescreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.onewaytripcalltaxi.driver.R

class HomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_screen)

        val profileImageView = findViewById<ShapeableImageView>(R.id.profileImageView)

        Glide.with(this)
            .load("https://fastly.picsum.photos/id/292/200/300.jpg?hmac=zm-TXplXe70N7LGm2HWu9iOPXoBtQvwyhAF2CSj0ccs") // Your drawable resource
            .into(profileImageView)


    }
}