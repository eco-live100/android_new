package com.app.ecolive.pharmacy_module.model

data class SearchMedicineList(
    val data: List<SearchMedicineListData>,
    val message: String,
    val statusCode: Int
)