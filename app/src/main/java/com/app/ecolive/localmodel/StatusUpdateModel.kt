package com.app.ecolive.localmodel

data class StatusUpdateModel(
    val message: String,
    val order: Order
)
{
data class Order(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val orderNumber: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val products: List<Product>,
    val shippingAddress: String,
    val shopId: String,
    val status: String,
    val totalAmount: Int,
    val updatedAt: String,
    val userId: String
)

data class Product(
    val _id: String,
    val price: Int,
    val productId: String,
    val quantity: Int,
    val vendorShopId: String
)}