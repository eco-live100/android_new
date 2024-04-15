package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class MedicineDataModel(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val description: String,
    val expireDate: String,
    val medicineName: String,
    val pharmacyId: String,
    val precautions: String,
    val quantity: String,
    val updatedAt: String,
    val price: String,
    val image: String,
): Serializable
