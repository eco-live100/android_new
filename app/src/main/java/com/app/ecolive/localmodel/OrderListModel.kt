package com.app.ecolive.localmodel

data class OrderListModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val count: Int,
    val items: ArrayList<Item>,
    val limit: String,
    val page: String
)

data class Item(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val orderNumber: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val products: ArrayList<Product>,
    val shippingAddress: String,
    var status: String,
    val totalAmount: Double,
    val updatedAt: String,
    val userId: String
)

data class Product(
    val _id: String,
    val price: Double,
    val productId: String,
    val quantity: Int,
    val vendorShopId: String
)}