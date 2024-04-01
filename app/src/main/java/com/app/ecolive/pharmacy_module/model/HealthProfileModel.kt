package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class HealthProfileModel(
    val data: HealthProfileData,
    val message: String,
    val statusCode: Int
): Serializable