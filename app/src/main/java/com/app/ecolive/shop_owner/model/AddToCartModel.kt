package com.app.ecolive.shop_owner.model

data class AddToCartModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val products: List<Product>,
    val shopId: String,
    val totalPrice: Int,
    val totalQty: Int,
    val updatedAt: String,
    val userId: String
)

data class Product(
    val _id: String,
    val price: Int,
    val productId: String,
    val qty: Int
)}