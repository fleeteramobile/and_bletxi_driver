package com.onewaytripcalltaxi.driver.locationSearch

import java.util.*

interface PlaceSearchList {
    fun setPlaceList(placeDetailResult: ArrayList<PlacesDetail>?)
    fun setPlaceDetail(placeDetail: PlacesDetail)
}