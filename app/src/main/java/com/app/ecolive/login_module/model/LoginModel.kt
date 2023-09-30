package com.app.ecolive.login_module.model

data class LoginModel(
    val `data`: Data,
    val message: String,
    val response: String,
    val statusCode: Int
) {
    data class Data(
        val _id: String,
        val accessToken: String,
        val checkEmailVerified: CheckEmailVerified,
        val checkMobileVerified: CheckMobileVerified,
        val countryCode: String,
        val createdAt: String,
        val email: String,
        val fcmToken: String,
     //   val fullName: String,
        val firstName: String,
        val lastName: String,
        val getNotification: Boolean,
        var isRider: Boolean,
        val isSocialLogin: Boolean,
        var isVendor: Boolean,
        val language: String,
        val latitude: String,
        val longitude: String,
        val profilePicture: Any?,
        val mobileNumber: String,
        val notificationFound: Boolean,
        val refereshToken: String,
        val role: String,
        val status: String,
        val updatedAt: String,
        val socialLoginType: String,
        val socialLoginId: String
    )

    data class CheckEmailVerified(
        val emailVerified: Boolean
    )

    data class CheckMobileVerified(
        val mobileVerified: Boolean
    )

}