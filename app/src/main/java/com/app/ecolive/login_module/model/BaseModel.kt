package com.app.ecolive.login_module.model

class BaseModel(
    val `data`: LoginModel.Data,
    val message: String,
    val response: String,
    val statusCode: Int
) {}