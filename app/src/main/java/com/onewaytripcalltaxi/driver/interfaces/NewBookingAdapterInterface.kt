package com.onewaytripcalltaxi.driver.interfaces

import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse

interface NewBookingAdapterInterface {
    fun newbookingUpcomingAdapter(data : List<UpcomingResponse.ShowBooking>, clickedPosition : Int)
}