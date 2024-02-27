package com.app.ecolive.pharmacy_module.model

data class UserPrescriptionModel(
    val data: List<PrescriptionRequestData>,
    val message: String,
    val statusCode: Int
)