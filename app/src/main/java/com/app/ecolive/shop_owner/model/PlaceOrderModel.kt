package com.app.ecolive.shop_owner.model

data class PlaceOrderModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)
{
data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val orderNumber: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val products: List<Product>,
    val shippingAddress: String,
    val status: String,
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
)
    data class productData(
        var vendorShopId:String,
        var productId:String,
        var quantity:Int,
        var price:Any,
    )
}

