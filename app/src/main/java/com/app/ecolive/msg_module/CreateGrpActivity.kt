package com.app.ecolive.msg_module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.CreateGrpActivityBinding
import com.app.ecolive.utils.Utils

class CreateGrpActivity : AppCompatActivity() {

    lateinit var binding: CreateGrpActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@CreateGrpActivity,R.layout.create_grp_activity)
        setToolBar()


    }


    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)

        binding.toolbarChatList.toolbarTitle.text="Messaging"
        binding.toolbarChatList.ivBack.setOnClickListener { finish() }
    }
}