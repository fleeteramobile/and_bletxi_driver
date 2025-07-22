package com.bluetaxi.driver.triplist.adapter

import com.bluetaxi.driver.triplist.model.ResponseOngoingBooking
import com.bluetaxi.driver.triplist.model.ResponseOutstationTripList
import com.bluetaxi.driver.triplist.model.ResponseTollTripList


interface OngoingTrip {

    fun startTrip(_category: ResponseOngoingBooking.Detail.PendingBooking)
    fun contactPassenger(_category: ResponseOngoingBooking.Detail.PendingBooking)
    fun cancelTrip(_category: ResponseOngoingBooking.Detail.PendingBooking)
    fun trackTrip(_category: ResponseOngoingBooking.Detail.PendingBooking)

}