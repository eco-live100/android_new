package com.app.ecolive.shop_owner.model

data class GetCartModel(
    val `data`: List<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val productMaxQuantity: Int,
        val product_color: String,
        val product_id: String,
        val products: Products,
        val purchase_type: String,
        val qty: String,
        val shop: Shop,
        val shop_id: String,
        val updatedAt: String,
        val user: String
    )

    data class Products(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val fastDeliver: String,
        val freeDelivery: String,
        val imageUrl: List<ImageUrl>,
        val images: List<Image>,
        val productData: ProductData,
        val shopCategoryId: String,
        val shopSubCategoryId: String,
        val updatedAt: String,
        val userId: String,
        val vendorShopId: String
    )

    data class Shop(
        val __v: Int,
        val _id: String,
        val coordinates: List<Double>,
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
        val shopSubCategoryId: String,
        val shopType: String,
        val status: Boolean,
        val storeAddress: String,
        val storeDocuments: List<String>,
        val storeLogo: String,
        val updatedAt: String,
        val userId: String
    )

    data class ImageUrl(
        val name: String
    )

    data class Image(
        val name: String
    )

    data class ProductData(
        val brand: String,
        val color: List<String>,
        val description: String,
        val price: String,
        val priceLive: String,
        val productFor: String,
        val productName: String,
        val productSize: String,
        val skuNo: String,
        val subCatogary: String
    )

}