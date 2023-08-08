package com.app.ecolive.localmodel

import android.graphics.drawable.Drawable
import java.io.Serializable


data class ReviewRiderToCustomerModel (
    var name: String,
    var reviewTime: String,
    var ratingCount: Float,
    var comment :String


)