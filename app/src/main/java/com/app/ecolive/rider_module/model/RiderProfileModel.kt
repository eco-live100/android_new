package com.app.ecolive.rider_module.model

data class RiderProfileModel(
    val data: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val countryCode: String,
        val createdAt: String,
        val deviceId: String,
        val deviceType: String,
        val email: String,
        val fcmToken: String,
        val firstName: String,
        val getNotification: Boolean,
        val isRider: Boolean,
        val isRiderVerified: Boolean,
        val isSocialLogin: Boolean,
        val isVendor: Boolean,
        val isVendorVerified: Boolean,
        val language: String,
        val lastName: String,
        val latitude: String,
        val longitude: String,
        val mobileNumber: String,
        val notificationFound: Boolean,
        val password: String,
        val role: String,
        val socialLoginId: String,
        val socialLoginType: String,
        val status: String,
        val totalBookings: Int,
        val totalEarning: String,
        val todayBookings: Int,
        val todayEarning: String,
        val updatedAt: String,
        val driverOnDuty: Boolean,
    )
}