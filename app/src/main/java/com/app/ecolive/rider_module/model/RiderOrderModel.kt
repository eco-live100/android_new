package com.app.ecolive.rider_module.model

data class RiderOrderModel(
    val data: List<RiderOrderData>,
    val message: String,
    val statusCode: Int
)