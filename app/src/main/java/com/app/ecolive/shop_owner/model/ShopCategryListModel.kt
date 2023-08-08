package com.app.ecolive.shop_owner.model

data class ShopCategryListModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val response: String,
    val statusCode: Int
) {

    data class Data(
        val _id: String,
        val categoryName: String,
        val subCategories: List<SubCategory>
    )

    data class SubCategory(
        val _id: String,
        val shopCategoryId: String,
        val subCategoryName: String
    )
}