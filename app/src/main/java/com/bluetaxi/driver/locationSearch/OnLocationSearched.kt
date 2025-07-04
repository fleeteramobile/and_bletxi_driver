package com.bluetaxi.driver.locationSearch


interface OnLocationSearched {
    fun onLocationSearched(queryString: String)
    fun onItemClicked(placesDetail: PlacesDetail)
}