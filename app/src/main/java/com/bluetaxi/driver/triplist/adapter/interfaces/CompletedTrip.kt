package com.bluetaxi.driver.triplist.adapter.interfaces

import com.bluetaxi.driver.triplist.model.ResponseOngoingBooking
import com.bluetaxi.driver.triplist.model.ResponseOutstationTripList
import com.bluetaxi.driver.triplist.model.ResponsePastBooking
import com.bluetaxi.driver.triplist.model.ResponseTollTripList


interface CompletedTrip {

    fun showTripDetails(_category: ResponsePastBooking.Detail.PastBooking)


}