package com.app.ecolive.pharmacy_module.model

data class MedicineListModel(
    val data: List<MedicineDataModel>,
    val message: String,
    val statusCode: Int
)