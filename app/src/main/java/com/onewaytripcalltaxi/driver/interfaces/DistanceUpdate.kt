package com.onewaytripcalltaxi.driver.interfaces

interface DistanceUpdate {
    fun onDistanceUpdate(distance: Double?, s: String)
}