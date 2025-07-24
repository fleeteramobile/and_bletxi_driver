package com.bluetaxi.driver.triplist.adapter.interfaces

import com.bluetaxi.driver.triplist.model.ResponseOutstationTripList
import com.bluetaxi.driver.triplist.model.ResponseTollTripList


interface OutstationStartTrip {

    fun startTrip(_category: ResponseOutstationTripList.Detail.PendingBooking)
    fun contactPassenger(_category: ResponseOutstationTripList.Detail.PendingBooking)
    fun cancelTrip(_category: ResponseOutstationTripList.Detail.PendingBooking)



}