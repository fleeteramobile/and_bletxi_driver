package com.bluetaxi.driver.triplist.model

data class ResponseOngoingBooking(
    val detail: Detail,
    val message: String,
    val status: Int
) {
    data class Detail(
        val past_booking: List<Any>,
        val pending_booking: List<PendingBooking>
    ) {
        data class PendingBooking(
            val _id: Int,
            val approx_fare: Int,
            val away: String,
            val bookby: Int,
            val driver_name: String,
            val drivername: String,
            val drop_latitude: Double,
            val drop_location: String,
            val drop_longitude: Double,
            val dynamic_fare: Int,
            val fare_type: Int,
            val map_image: String,
            val model_fare_stage1_fare: Int,
            val model_fare_stage1_from: Int,
            val model_fare_stage1_to: Int,
            val model_fare_stage2_fare: Int,
            val model_fare_stage2_from: Int,
            val model_fare_stage2_to: Int,
            val model_fare_stage3_fare: Int,
            val model_fare_stage3_from: Int,
            val model_fare_stage3_to: Int,
            val notes: String,
            val passenger_country_code: String,
            val passenger_name: String,
            val passenger_phone: String,
            val passenger_profile_image: String,
            val passengers_log_id: String,
            val pay_mod_id: String,
            val payment_type: String,
            val pickup_latitude: Double,
            val pickup_location: String,
            val pickup_longitude: Double,
            val pickup_time: String,
            val pickup_time_text: String,
            val profile_image: String,
            val route_path: String,
            val schedule: Int,
            val time: String,
            val travel_status: Int,
            val trip_location: TripLocation,
            val waiting_hour: String
        ) {
            data class TripLocation(
                val coordinates: List<Double>,
                val type: String
            )
        }
    }
}