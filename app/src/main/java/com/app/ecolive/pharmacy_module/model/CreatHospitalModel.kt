package com.app.ecolive.pharmacy_module.model

data class CreatHospitalModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val __v: Int,
    val _id: String,
    val address: String,
    val allowPublicToViewEmployees: Boolean,
    val cosultationFees: Int,
    val createdAt: String,
    val fullName: String,
    val latitude: Double,
    val longitude: Double,
    val services: List<String>,
    val updatedAt: String,
    val userId: String
)}