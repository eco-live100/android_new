package com.app.ecolive.shop_owner.model

data class PlaceOrderModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val orderNumber: String,
        val paymentMethod: String,
        val paymentStatus: String,
        val products: List<Product>,
        val shippingAddress: String,
        val shopId: ShopId,
        val status: String,
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
        val images: List<Image>,
        val livePrice: Int,
        val name: String,
        val price: Int
    )

    data class Image(
        val name: String
    )

    data class productData(
        var vendorShopId:String,
        var productId:String,
        var quantity:Int,
        var price:Any,
    )
}