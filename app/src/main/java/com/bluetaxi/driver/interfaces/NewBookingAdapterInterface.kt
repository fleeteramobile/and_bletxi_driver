package com.bluetaxi.driver.interfaces

import com.bluetaxi.driver.data.apiData.UpcomingResponse

interface NewBookingAdapterInterface {
    fun newbookingUpcomingAdapter(data : List<UpcomingResponse.ShowBooking>, clickedPosition : Int)
}