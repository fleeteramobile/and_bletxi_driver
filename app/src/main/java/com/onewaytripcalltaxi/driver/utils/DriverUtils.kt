package com.onewaytripcalltaxi.driver.utils

import android.content.Context
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.data.ModelDriverInfo
import com.onewaytripcalltaxi.driver.service.LocationUpdate.*


/**
 * Created on 10th October by developer at NDOT Technologies
 * Singleton class to get driver information like id,shift status,etc ...
 *
 * object represents singleton instance for this class
 * <b>https://kotlinlang.org/docs/reference/object-declarations.html</b>
 */
object DriverUtils {
    fun driverInfo(context: Context): ModelDriverInfo {
        val driverLastLocation = "$currentLatitude,${currentLongtitude}"
        val driverLocationAccuracy = "$currentAccuracy"
        return ModelDriverInfo(SessionSave.getSession("Id", context),
                SessionSave.getSession("trip_id", context), "$driverLastLocation,$driverLocationAccuracy", SessionSave.getSession("shift_status", context), SessionSave.getSession("travel_status", context),
                SessionSave.getSession("service_status", context, false),
                SessionSave.getSession(CommonData.DRIVER_LOCATION_STATIC, context).replace("null", ""))
    }
}