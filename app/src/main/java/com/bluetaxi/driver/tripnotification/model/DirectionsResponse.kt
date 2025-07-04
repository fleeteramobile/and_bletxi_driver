package com.bluetaxi.driver.tripnotification.model

import com.google.gson.annotations.SerializedName


data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>,
    @SerializedName("status") val status: String
)
