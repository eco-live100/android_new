package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class PharmacyOrderListModel(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val deliveryStatus: Int?,
    val pharmacyId: String?,
    val status: Int?,
    val updatedAt: String?,
    val userId: String?,
    val precriptionDetails: PrescriptionDataModel?,
):Serializable