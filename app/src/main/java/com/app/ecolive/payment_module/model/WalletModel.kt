package com.app.ecolive.payment_module.model

data class WalletModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val __v: Int,
        val _id: String,
        val createdAt: String,
        val currency: String,
        val money: Double,
        val status: Boolean,
        val updatedAt: String,
        val userId: String,
        val requestedAmount: Int,
        val method: String,
        val paymentUrl: String,
    )
}