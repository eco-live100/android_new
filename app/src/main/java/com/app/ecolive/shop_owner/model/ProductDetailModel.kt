package com.app.ecolive.shop_owner.model

data class ProductDetailModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val fastDeliver: String,
    val freeDelivery: String,
    val images: ArrayList<Image>,
    val livePrice: Any,
    val name: String,
    val outofstock: Boolean,
    val price: Any,
    val productData: String,
    val shopCategoryId: ShopCategoryId,
    val shopSubCategoryId: ShopSubCategoryId,
    val updatedAt: String,
    val userId: UserId,
    val vendorShopId: String
)

data class Image(
    val name: String
)

data class ShopCategoryId(
    val _id: String,
    val categoryName: String
)

data class ShopSubCategoryId(
    val _id: String,
    val subCategoryName: String
)

data class UserId(
    val _id: String,
    val email: String
)}