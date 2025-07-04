package com.bluetaxi.driver.interfaces

interface DistanceUpdate {
    fun onDistanceUpdate(distance: Double?, s: String)
}