package com.app.ecolive.pharmacy_module.model

data class AddMedicineModel(
    val data: MedicineDataModel,
    val message: String,
    val statusCode: Int
)