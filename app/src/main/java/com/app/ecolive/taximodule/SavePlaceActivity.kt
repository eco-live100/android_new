package com.app.ecolive.taximodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivitySavePlaceBinding
import com.app.ecolive.utils.Utils

class SavePlaceActivity : AppCompatActivity() {
    lateinit var  binding: ActivitySavePlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_save_place)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Choose a destination"
        binding.imgArrow.setOnClickListener {
            startActivity(Intent(this,SavelocationActivity::class.java))
        }

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
    }
}