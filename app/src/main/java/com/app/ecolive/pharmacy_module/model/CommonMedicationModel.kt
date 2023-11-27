package com.app.ecolive.pharmacy_module.model

data class CommonMedicationModel(
    val data: List<Data>,
    val message: String,
    val statusCode: Int
){
    data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val name: String,
        val updatedAt: String
    )
}

