package com.app.ecolive.taximodule.model

data class VehicleModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val _id: String,
    val amount: String,
    val dropOffAt: String,
    val logo: String,
    val taxiCategory: String,
    val taxiType: String,
    val vehicleTypeColor: String
)}