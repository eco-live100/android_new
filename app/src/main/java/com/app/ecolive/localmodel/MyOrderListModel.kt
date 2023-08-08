package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class MyOrderListModel (
    var rating:String,
    var productName: String,
    var deliveredDate: String,
    var amount: String,
    var image: Drawable,

)