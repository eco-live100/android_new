package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class AddProductImageListModel (
    val viewType: Int,
    var addImage: Drawable
)