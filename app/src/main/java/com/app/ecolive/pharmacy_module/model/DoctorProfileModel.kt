package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class DoctorProfileModel(
    val data: DoctorDetailModel?,
    val message: String,
    val statusCode: Int
): Serializable