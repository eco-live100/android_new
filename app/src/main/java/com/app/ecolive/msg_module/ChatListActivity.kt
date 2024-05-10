package com.app.ecolive.msg_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ChatlistActivityBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.rider_module.HomeRiderActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.utils.PopUpVehicleChoose
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils

class ChatListActivity : AppCompatActivity() {

    lateinit var binding: ChatlistActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@ChatListActivity,R.layout.chatlist_activity)
        setToolBar()


        binding.chatCreateGrp.setOnClickListener {
            startActivity(Intent(this@ChatListActivity,CreateGrpActivity::class.java))
        }



    }



    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)

        binding.toolbarChatList.toolbarTitle.text="Messaging"
        binding.toolbarChatList.cutmToolBarRightIcon2.visibility=View.INVISIBLE
        binding.toolbarChatList.ivUserImage.visibility=View.GONE
        binding.toolbarChatList.ivBack.visibility=View.VISIBLE
        binding.toolbarChatList.ivBack.setOnClickListener { finish() }
    }



}