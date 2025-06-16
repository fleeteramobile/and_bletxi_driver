package com.onewaytripcalltaxi.driver.triplist

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.onewaytripcalltaxi.driver.R

class CommonTripHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_trip_history)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val back_trip_details = findViewById<CardView>(R.id.back_trip_details)
        back_trip_details.setOnClickListener {
            onBackPressed()
        }

        val adapter = ViewPagerAdapter(this)

        // Set the adapter to the ViewPager2
        viewPager.adapter = adapter

        // Link the TabLayout and ViewPager2 together
        // This makes sure that selecting a tab updates the ViewPager and
        // swiping the ViewPager updates the selected tab.
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Set the text for each tab based on its position
            tab.text = when (position) {
                0 -> "Upcoming" // Text for the first tab
                1 -> "Completed" // Text for the second tab
                2 -> "New Booking" // Text for the third tab
                else -> "" // Default empty string
            }
        }.attach() // Attach the mediator to complete the setup
    }
    }

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // Returns the total number of fragments (tabs)
    override fun getItemCount(): Int {
        return 3 // We have 3 tabs: Upcoming, Completed, New Booking
    }

    // Creates and returns the appropriate fragment for a given position
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UpcomingFragment() // First tab: Upcoming
            1 -> CompletedFragment() // Second tab: Completed
            2 -> NewBookingFragment() // Third tab: New Booking
            else -> throw IllegalArgumentException("Invalid position: $position") // Handle unexpected positions
        }
    }
}
