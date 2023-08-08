package com.app.ecolive.rider_module.model

data class VehicalCatgryListModel(
    val `data`: Data,
    val message: String,
    val response: String,
    val statusCode: Int
) {

    data class Data(
        val vehicleCategory: ArrayList<VehicleCategory>,
        val vehicleType: ArrayList<VehicleType>
    )

    data class VehicleCategory(
        val _id: String,
        val title: String
    )

    data class VehicleType(
        val _id: String,
        val title: String,
        val vehicleTypeColor: String
    )
}