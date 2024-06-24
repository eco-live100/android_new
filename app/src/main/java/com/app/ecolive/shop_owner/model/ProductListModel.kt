package com.app.ecolive.shop_owner.model

data class ProductListModel(
    val count: Int,
    val items: ArrayList<Item>,
    val limit: String,
    val page: String
)

data class Item(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val fastDeliver: String,
    val freeDelivery: String,
    val images: ArrayList<Image>,
    val livePrice: Int,
    val name: String,
    var outofstock: Boolean,
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
)