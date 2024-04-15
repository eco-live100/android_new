package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class HealthProfileData(
    val __v: Int?,
    val _id: String?,
    val address: String?,
    val createdAt: String?,
    val insurance: String?,
    val commonMedication: List<MedicineDataModel>?,
    val name: String?,
    val ssn: String?,
    val updatedAt: String?,
    val userId: String?
): Serializable