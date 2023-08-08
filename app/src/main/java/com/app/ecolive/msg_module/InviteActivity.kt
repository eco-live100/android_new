package com.app.ecolive.msg_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityInviteBinding
import com.app.ecolive.utils.Utils

class InviteActivity : AppCompatActivity() {
    lateinit var binding: ActivityInviteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_invite)
        binding.toolbar.ivCart.visibility =View.VISIBLE
        binding.toolbar.toolbarTitle.text="+108106496"
        binding.toolbar.ivCart.setImageResource(R.drawable.ic_call_white_top17)
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        setToolBar()

    }
    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)


    }
}