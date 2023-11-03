package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class CommonResponse <T>(
    val data: T?,
    val message: String?,
    val statusCode: Int?,
):Serializable

