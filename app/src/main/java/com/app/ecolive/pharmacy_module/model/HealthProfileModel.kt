package com.app.ecolive.pharmacy_module.model

data class HealthProfileModel(
    val data: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val address: String,
        val createdAt: String,
        val insurance: List<Any>,
        val medications: List<String>,
        val name: String,
        val ssn: String,
        val updatedAt: String,
        val userId: String
    ) {
        data class Insurance(
            val name: String
        )
    }
}