package com.app.ecolive.shop_owner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityRestaurantProfileBinding
import com.app.ecolive.shop_owner.adapters.FoodItemAdapter
import com.app.ecolive.user_module.user_adapter.FoodListAdapter
import com.app.ecolive.utils.Utils

class RestaurantProfileActivity : AppCompatActivity() {
    lateinit var binding:ActivityRestaurantProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this,R.layout.activity_restaurant_profile)
        Utils.changeStatusTextColor2(this)
        binding.ivBack.setOnClickListener{finish()}
        binding.edit.setOnClickListener {
            startActivity(
                Intent(
                    this@RestaurantProfileActivity,
                    AddResturantItemActivity::class.java
                ).putExtra("from","food")
            )
        }
        binding.recycleViewFood.layoutManager =
            GridLayoutManager(this,2 )
        val foodListAdapter = FoodItemAdapter(
            this@RestaurantProfileActivity,

            object : FoodItemAdapter.ClickListener {
                override fun onClick(pos: Int) {
                    startActivity(
                        Intent(
                            this@RestaurantProfileActivity,
                            RestaurantProfileActivity::class.java
                        )
                    )
                }

            })
        binding.recycleViewFood.adapter = foodListAdapter
    }
}