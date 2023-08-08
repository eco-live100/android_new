package com.app.ecolive.msg_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityWallpaperBinding
import com.app.ecolive.utils.Utils

class WallpaperActivity : AppCompatActivity() {
    lateinit var binding :ActivityWallpaperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_wallpaper)
        binding.include6.toolbarTitle.text ="Select chat background"
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.include6.ivBack.setOnClickListener {
            finish()
        }
    }
}