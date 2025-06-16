package com.onewaytripcalltaxi.driver.triplist

data class NewBookings(
    var passengers_log_id: String? = null,
    var pickup_location: String? = null,
    var drop_location: String? = null,
    var pickup_time: String? = null,
    var time: String? = null,
    var away: String? = null,
    var pickup_latitude: String? = null,
    var pickup_longitude: String? = null,
    var drop_latitude: String? = null,
    var drop_longitude: String? = null)
