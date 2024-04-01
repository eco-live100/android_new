package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class PharmacyData(

    val __v: Int,
    val _id: String,
    val activeStatus: Boolean,
    val createdAt: String,
    val distance: Double,
    val latitude: Double,
    val licenceImage: String,
    val licenceNumber: String,
    val location: String,
    val longitude: Double,
    val mobileNumber: String,
    val openingFrom: String,
    val openingTo: String,
    val pharmacyImage: String,
    val pharmacyName: String,
    val repeatForWeek: Boolean,
    val updatedAt: String,
    val userId: String,

    val todaysOrder: Int,
) : Serializable
