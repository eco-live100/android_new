package com.app.ecolive.localmodel

data class OrderListModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
){

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
    val shopId: ShopId,
    var status: String,
    val totalAmount: Int,
    val updatedAt: String,
    val userId: UserId
)

data class Product(
    val _id: String,
    val price: Int,
    val productId: ProductId,
    val quantity: Int,
    val vendorShopId: String
)

data class ShopId(
    val _id: String,
    val email: String,
    val shopName: String
)

data class UserId(
    val _id: String,
    val email: String
)

data class ProductId(
    val _id: String,
    val images: ArrayList<Image>,
    val livePrice: Int,
    val name: String,
    val price: Int
)

data class Image(
    val name: String
)}