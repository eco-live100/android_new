package com.app.ecolive.notification

import java.io.Serializable

data class NotificationModel(
    val title: String="Booking Request",
    val body: String="Someone request for taxi",
    val amount: String,
    val bookingNumber: String,
    val bookingStatus: String,
    val bookingType: String,
    val distanceInKm: String,
    val fromAddress: String,
    val fromDate: String,
    val fromLatitude: String,
    val fromLongitude: String,
    val paymentType: String,
    val pickUpTimeFrom: String,
    val pickUpTimeTo: String,
    val taxiId: String,
    val toAddress: String,
    val toDate: String,
    val toLatitude: String,
    val toLongitude: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val userProfilePicture: String
):Serializable