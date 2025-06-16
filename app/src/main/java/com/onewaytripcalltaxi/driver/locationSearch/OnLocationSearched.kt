package com.onewaytripcalltaxi.driver.locationSearch


interface OnLocationSearched {
    fun onLocationSearched(queryString: String)
    fun onItemClicked(placesDetail: PlacesDetail)
}