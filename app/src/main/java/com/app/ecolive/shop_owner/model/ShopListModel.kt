package com.app.ecolive.shop_owner.model

import java.io.Serializable

data class ShopListModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val response: String,
    val statusCode: Int
):Serializable {
    data class Data(
        val _id: String,
        val countryCode: String,
        val createdAt: String,
        val email: String,
        val firstName: String,
        val isVerified: Boolean,
        val lastName: String,
        val latitude: String,
        val longitude: String,
        val mobileNumber: String,
        val numberOfLocation: String,
        val shopCategoryId: String,
        val shopDescription: String,
        val shopName: String,
        val shopType: String,
        val storeAddress: String,
        val shopSubCategoryId: String,
        val status: Boolean,
        val storeDocuments: ArrayList<String>,
        val storeLogo: String? = null,
        val updatedAt: String,
        val userId: String
    ):Serializable
}