package com.app.ecolive.shop_owner.model

data class ProductModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {

    data class Data(
        val docs: List<Doc>,
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
        val fastDeliver: String,
        val fastDeliveryTime: String,
        val `file`: List<File>,
        val freeDelivery: String,
        val freeDeliveryTime: String,
        val offers: List<Any>,
        val productData: ProductData,
        val productDescription: String,
        val shopCategoryId: String,
        val shopSubCategoryId: String,
        val storeId: String
    )

    data class File(
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