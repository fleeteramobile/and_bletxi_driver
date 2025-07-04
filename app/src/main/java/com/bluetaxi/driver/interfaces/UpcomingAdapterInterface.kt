package com.bluetaxi.driver.interfaces

import com.bluetaxi.driver.data.apiData.UpcomingResponse

interface UpcomingAdapterInterface {
    fun updateUpcomingAdapter(data : List<UpcomingResponse.PastBooking>, clickedPosition : Int)
}