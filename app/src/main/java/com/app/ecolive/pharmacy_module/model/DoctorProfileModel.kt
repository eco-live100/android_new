package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class DoctorProfileModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val backgroungPicture: String,
        val consultFees: Int,
        val createdAt: String,
        val fullName: String,
        val idNumber: String,
        val isRepeated: Boolean,
        val latitude: Double,
        val location: String,
        val logo: String,
        val longitude: Double,
        val mobileNumber: String,
        val primaryVisitingHour: String,
        val profession: String,
        val services: String,
        val updatedAt: String,
        val userId: String,
        val secondryVisitingHour: String,
    ):Serializable
}