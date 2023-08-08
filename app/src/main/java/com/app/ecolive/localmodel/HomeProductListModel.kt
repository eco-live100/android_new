package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class HomeProductListModel (

    var name: String,
    var shopOnlinePrice: String,
    var shopLivePrice: String,
    var image: Drawable,
    var imageUrl: String,
    var productId: String="",

)