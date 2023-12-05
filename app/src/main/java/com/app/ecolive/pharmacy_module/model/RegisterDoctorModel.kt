package com.app.ecolive.pharmacy_module.model

data class RegisterDoctorModel(
    val data: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val consultFees: Int,
        val createdAt: String,
        val fullName: String,
        val idNumber: String,
        val isRepeated: Boolean,
        val latitude: Double,
        val location: String,
        val longitude: Double,
        val mobileNumber: String,
        val primaryVisitingHour: String,
        val profession: String,
        val secondryVisitingHour: String,
        val services: String,
        val updatedAt: String,
        val userId: String
    )
}