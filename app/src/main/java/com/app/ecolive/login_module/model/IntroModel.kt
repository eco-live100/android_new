package com.app.ecolive.login_module.model

data class IntroModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val response: String,
    val statusCode: Int
) {
    data class Data(
        val banner: String,
        val content: String,
        val description: String,
        val title: String
    )
}