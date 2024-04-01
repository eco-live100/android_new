package com.app.ecolive.login_module.model

data class LoginModel(
    val data: UserModel,
    val message: String,
    val response: String,
    val statusCode: Int
)

data class CheckEmailVerified(
    val emailVerified: Boolean
)

data class CheckMobileVerified(
    val mobileVerified: Boolean
)
