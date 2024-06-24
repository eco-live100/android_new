package com.app.ecolive.shop_owner.model

data class ProductModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val docs: ArrayList<Doc>,
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val limit: Int,
    val nextPage: Any,
    val page: Int,
    val pagingCounter: Int,
    val prevPage: Any,
    val totalDocs: Int,
    val totalPages: Int
)

data class Doc(
    val _id: String,
    val createdAt: String,
    val fastDeliver: String,
    val freeDelivery: String,
    val images: ArrayList<String>,
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
)}