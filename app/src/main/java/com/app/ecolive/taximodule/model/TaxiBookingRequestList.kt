package com.app.ecolive.taximodule.model

data class TaxiBookingRequestList(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(

        val driverAddress: String,
        val driverId: String,
        val driverLatitude: String,
        val driverLongitude: String,
        val userAddress: String,
        val userLatitude: String,
        val userLongitude: String,


        val __v: Int,
        val _id: String,
        val amount: String,
        val bookingNumber: String,
        val bookingStatus: String,
        val bookingType: Int,
        val createdAt: String,
        val distanceInKm: String,
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
        val userId: String
    )

    {
        data class TaxiId(
            val __v: Int,
            val _id: String,
            val createdAt: String,
            val driverId: String,
            val driverOnDuty: Boolean,
            val taxiLatitude: String,
            val taxiLongitude: String,
            val taxiName: String,
            val taxiNumber: String,
            val updatedAt: String
        )
    }
}