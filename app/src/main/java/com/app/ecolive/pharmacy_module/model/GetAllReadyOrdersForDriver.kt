package com.app.ecolive.pharmacy_module.model

import java.io.Serializable

data class GetAllReadyOrdersForDriver(
    val count: Int,
    val items: List<GetAllReadyOrdersForDriverData>,
    val limit: String,
    val page: String
) : Serializable