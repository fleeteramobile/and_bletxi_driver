package com.onewaytripcalltaxi.driver.interfaces

import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse

interface UpcomingAdapterInterface {
    fun updateUpcomingAdapter(data : List<UpcomingResponse.PastBooking>, clickedPosition : Int)
}