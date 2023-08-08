package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class CurrentOrderListModel (
    var currentOrderUserName: String,
    var currentOrderDate: String,
    var currentOrderTime: String,
    var currentDeliveryEstimateTime: String,
    var currentOrderEarning: String,
    var currentOrderPaymentMode: String,
    var image: Drawable,

)