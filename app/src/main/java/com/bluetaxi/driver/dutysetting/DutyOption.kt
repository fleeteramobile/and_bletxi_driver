package com.bluetaxi.driver.dutysetting

data class DutyOption(
    val modelId: Int,
    val modelName: String,
    val isMyVehicle: Boolean,
    val modelImage: String,
    var isEnabled: Boolean,
    val isSwitchEnabled: Boolean
)

