package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class AcceptDeclineOrderListModel (
    var orderId: String,
    var orderDate: String,
    var orderTime: String,
    var orderName: String,
    var orderItem1: String,
    var orderItem2: String,
    var orderItemPrice1: String,
    var orderItemPrice2: String,
    var orderTotal: String,
    var orderStatus: String,


)