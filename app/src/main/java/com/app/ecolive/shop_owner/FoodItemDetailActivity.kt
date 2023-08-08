package com.app.ecolive.shop_owner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.ecolive.R
import com.app.ecolive.utils.Utils

class FoodItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_detail)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
    }
}