package com.bluetaxi.driver.triplist.adapter.interfaces

import com.bluetaxi.driver.triplist.model.ResponseTollTripList


interface TollAmountRequest {

    fun tollAmountRequest(_category: ResponseTollTripList.Detail.PastBooking)



}