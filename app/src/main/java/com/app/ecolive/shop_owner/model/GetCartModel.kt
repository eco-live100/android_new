package com.app.ecolive.shop_owner.model

data class GetCartModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
){

data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val products: ArrayList<Product>,
    val shopId: ShopId,
    val totalPrice: Int,
    val totalQty: Int,
    val updatedAt: String,
    val userId: UserId,
    val acknowledged: Boolean,
    val deletedCount: Int,

)

data class Product(
    val _id: String,
    val price: Int,
    val productId: ProductId,
    val qty: Int
)

data class ShopId(
    val _id: String,
    val email: String,
    val shopName: String,
    val userId: String
)

data class UserId(
    val _id: String,
    val email: String
)

data class ProductId(
    val _id: String,
    val firstImage: String,
    val livePrice: Int,
    val name: String,
    val price: Int
)}