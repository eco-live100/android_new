package com.app.ecolive.user_module.model

data class AddressModel(
    val code: Int,
    val `data`: ArrayList<Data>,
    val message: String
)
{
data class Data(
    val __v: Int,
    val _id: String,
    val addressType: String,
    val createdAt: String,
    val fullName: String,
    val lat: String,
    val long: String,
    val mobile: String,
    val status: Boolean,
    val title: String,
    val updatedAt: String,
    val userId: String
)}