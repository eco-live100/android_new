package com.app.ecolive.payment_module.model

data class TransactionHistoryModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val docs: ArrayList<Doc>,
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
        val __v: Int,
        val _id: String,
        val amount: String,
        val createdAt: String,
        val currency: String,
        val paymentFor: String,
        val paymentNote: String,
        val paymentStatus: String,
        val paymentType: String,
        val receiverDetails: ReceiverDetails,
        val receiverId: String,
        val senderId: String,
        val transactionDetails: List<Any>,
        val transactionId: String,
        val transactionType: String,
        val updatedAt: String
    )

    data class ReceiverDetails(
        val _id: String,
        val countryCode: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val mobileNumber: String,
        val profilePicture: Any
    )
}