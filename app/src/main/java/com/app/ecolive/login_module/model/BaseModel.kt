package com.app.ecolive.login_module.model

import com.app.ecolive.login_module.LoginModel

class BaseModel(
    val `data`: LoginModel.Data,
    val message: String,
    val response: String,
    val statusCode: Int
) {}