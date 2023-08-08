package com.app.ecolive.shop_owner.model

data class AttributeModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
){

data class Data(
    val attributes: List<Attribute>,
    val subCategoryName: String
)

data class Attribute(
    val `data`: List<String>,
    val `field`: String
)}