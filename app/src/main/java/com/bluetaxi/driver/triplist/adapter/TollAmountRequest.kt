package com.bluetaxi.driver.triplist.adapter

import com.bluetaxi.driver.triplist.model.ResponseTollTripList


interface TollAmountRequest {

    fun tollAmountRequest(_category: ResponseTollTripList.Detail.PastBooking)



}