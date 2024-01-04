package com.app.ecolive.pharmacy_module.model

data class UserPrescriptionModel(
    val data: List<UserPrescriptionData>,
    val message: String,
    val statusCode: Int
)