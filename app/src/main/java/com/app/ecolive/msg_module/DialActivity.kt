package com.app.ecolive.msg_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityDialBinding
import com.app.ecolive.utils.Utils

class DialActivity : AppCompatActivity() {
    lateinit var binding: ActivityDialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dial)
        setToolBar()
    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.imginvite.setOnClickListener {
            startActivity(Intent(this,InviteActivity::class.java))
        }
    }
}