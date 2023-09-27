package com.app.ecolive.taximodule.model
data class ConfirmTaxiModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val amount: String,
        val bookingNumber: String,
        val bookingStatus: String,
        val createdAt: String,
        val distanceInKm: String,
        val driverAddress: String,
        val driverId: String,
        val driverLatitude: String,
        val driverLongitude: String,
        val taxiId: String,
        val updatedAt: String,
        val userAddress: String,
        val userId: String,
        val userLatitude: String,
        val userLongitude: String
    )
}