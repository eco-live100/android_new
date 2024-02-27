package com.app.ecolive.pharmacy_module.model

data class PharmacyListData(
    val count: Int,
    val items: List<PharmacyData>,
    val limit: String,
    val page: String
)