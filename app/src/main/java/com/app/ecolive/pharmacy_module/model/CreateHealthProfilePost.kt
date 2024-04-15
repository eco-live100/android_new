package com.app.ecolive.pharmacy_module.model
data class CreateHealthProfilePost(
    val name: String,
    val address: String,
    val ssn: String,
    val commonMedication: List<SearchMedicineListData>
)