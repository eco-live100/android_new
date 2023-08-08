package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class TransactionHistoryListModel (
    var userName:String,
    var amount: String,
    var transactionDateTime: String,
    var status:String,
    var profileImage: Drawable,

)