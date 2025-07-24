package com.bluetaxi.driver.triplist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bluetaxi.driver.R
import com.bluetaxi.driver.dutysetting.DutySettingActivity
import com.bluetaxi.driver.gotohome.GotoHomeActivity
import com.bluetaxi.driver.triplist.pastbooking.PastBookingActivity
import com.bluetaxi.driver.triplist.upcoming.UpcomingTripsActivity
import com.bluetaxi.driver.utils.SessionSave

class BookingsActivity : AppCompatActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bookings)
        userNameTextView = findViewById(R.id.user_name)
        userEmailTextView = findViewById(R.id.user_email)
        userNameTextView.setText(SessionSave.getSession("driver_name",this@BookingsActivity))
        userEmailTextView.setText(SessionSave.getSession("driver_name",this@BookingsActivity))

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        // Click Listeners
        findViewById<LinearLayout>(R.id.my_booking_item).setOnClickListener {
             startActivity(Intent(this, UpcomingTripsActivity::class.java)) // example
        }

        findViewById<LinearLayout>(R.id.my_card_item).setOnClickListener {
            startActivity(Intent(this, PastBookingActivity::class.java)) // example

        }

        findViewById<LinearLayout>(R.id.outplaces_item).setOnClickListener {
            startActivity(Intent(this, OutstationUpcomingActivity::class.java)) // example
        }

        findViewById<LinearLayout>(R.id.saved_places_item).setOnClickListener {
            startActivity(Intent(this, TollRequestActivity::class.java)) // example

        }

        findViewById<LinearLayout>(R.id.settings_item).setOnClickListener {
            startActivity(Intent(this, GotoHomeActivity::class.java)) // example

        }

        findViewById<LinearLayout>(R.id.help_center_item).setOnClickListener {
            startActivity(Intent(this, DutySettingActivity::class.java)) // example

        }




    }
}