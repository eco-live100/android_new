package com.app.ecolive.shop_owner.model

data class ProductOutofStockModel(
    val message: String,
    val product: Product
){

data class Product(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val fastDeliver: String,
    val freeDelivery: String,
    val images: List<Image>,
    val livePrice: Int,
    val name: String,
    val outofstock: Boolean,
    val price: Int,
    val productData: String,
    val shopCategoryId: String,
    val shopSubCategoryId: String,
    val updatedAt: String,
    val userId: String,
    val vendorShopId: String
)

data class Image(
    val name: String
)}