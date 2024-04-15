package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class GetAllReadyOrdersForDriverData(
        val __v: Int,
        val _id: String,
    //val address: Address,
        val createdAt: String,
        val deliveryStatus: Int,
        val latitude: Int,
        val longitude: Int,
        val pharmacy: PharmacyData,
        val pharmacyId: String,
        val precriptionId: String,
        val status: Int,
        val updatedAt: String,
        val userId: String
    ): Serializable
