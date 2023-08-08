package com.app.ecolive.taximodule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAddStopBinding
import com.app.ecolive.utils.Utils

class AddStopActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddStopBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_stop)
        binding.toolbar.toolbarTitle.text ="Add stop"
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.menu1.setOnClickListener {
           startActivity(Intent(this,SavelocationActivity::class.java))
        }
    }
}