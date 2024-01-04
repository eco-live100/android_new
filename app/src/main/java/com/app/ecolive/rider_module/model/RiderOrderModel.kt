package com.app.ecolive.rider_module.model

import java.io.Serializable

data class RiderOrderModel(
    val data: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val amount: String,
        val bookingNumber: String,
        val bookingStatus: String,
        val bookingType: Int,
        val createdAt: String,
        val distanceInKm: String,
        val driverAddress: String,
        val driverId: String,
        val driverLatitude: String,
        val driverLongitude: String,
        val fromAddress: String,
        val fromDate: String,
        val fromLatitude: String,
        val fromLongitude: String,
        val paymentType: Int,
        val pickUpTimeFrom: String,
        val pickUpTimeTo: String,
        val taxiId: String,
        val toAddress: String,
        val toDate: String,
        val toLatitude: String,
        val toLongitude: String,
        val updatedAt: String,
        val userId: String,
        val userName: String,
        val userPhone: String,
        val userProfilePicture: String
    ):Serializable
}