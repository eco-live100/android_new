package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class PharmacyProfileModel(
    val data: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val activeStatus: Boolean,
        val createdAt: String,
        val licenceNumber: String,
        val location: String,
        val mobileNumber: String,
        val openingFrom: String,
        val openingTo: String,
        val pharmacyName: String,
        val repeatForWeek: Boolean,
        val updatedAt: String,
        val userId: String,
        val licenceImage: String,
        val pharmacyImage: String,
        val todaysOrder: Int
    ):Serializable
}