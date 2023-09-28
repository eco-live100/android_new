package com.app.ecolive.payment_module.model

  data class UserListModel(
    val code: Int,
    val `data`: ArrayList<Data>,
    val message: String
)

data class Data(
    val _id: String,
    val countryCode: String,
    val email: String,
    val firstName: String,
    val fullName: String,
    val lastName: String,
    val mobileNumber: String,
    val profilePicture: String,
    val role: String
)