package com.app.ecolive.msg_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityFriendsListBinding
import com.app.ecolive.msg_module.adapter.ChatListAdapter
import com.app.ecolive.msg_module.adapter.FriendListAdapter
import com.app.ecolive.user_module.FullImageActivity
import com.app.ecolive.utils.Utils

class FriendsListActivity : AppCompatActivity() {
    lateinit var binding :ActivityFriendsListBinding
    lateinit var friendlistadapter : FriendListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_friends_list)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Friend list(Eco-Live)"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        friendlistadapter = FriendListAdapter(this@FriendsListActivity,object: FriendListAdapter.ClickListener{
            override fun onClick(pos: Int) {
//                startActivity(Intent(this@FriendsListActivity,ChatActivity::class.java))
            }

        })
        binding.recycleFriends.adapter =friendlistadapter
        binding.recycleFriends.layoutManager =LinearLayoutManager(this)
    }
}