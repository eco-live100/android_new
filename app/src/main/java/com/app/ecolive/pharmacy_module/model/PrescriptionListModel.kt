package com.app.ecolive.pharmacy_module.model

data class PrescriptionListModel(
    val data: List<PrescriptionDataModel>,
    val message: String,
    val statusCode: Int
)