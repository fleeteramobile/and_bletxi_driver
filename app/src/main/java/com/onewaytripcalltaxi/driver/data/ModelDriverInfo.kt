package com.onewaytripcalltaxi.driver.data

data class ModelDriverInfo(val driverId: String = "",
                           val driverTripId: String = "",
                           val driverLastlocation: String = "",
                           val driverShiftStatus: String = "",
                           val travelStatus: String = "",
                           val serviceStatus: Boolean,
                           val currentLocation: String = "")
