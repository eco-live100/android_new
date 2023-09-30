package com.app.ecolive.taximodule.model

data class ScheduleRideModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val fromDate: String,
        val fromLatitude: String,
        val fromLongitude: String,
        val pickUpTime: String,
        val toDate: String,
        val toLatitude: String,
        val toLongitude: String,
        val updatedAt: String,
        val userId: String
    )
}