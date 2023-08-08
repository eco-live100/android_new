package com.app.ecolive.payment_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityFriendsListBinding
import com.app.ecolive.databinding.ActivityRecipentListBinding
import com.app.ecolive.msg_module.adapter.ChatListAdapter
import com.app.ecolive.msg_module.adapter.FriendListAdapter
import com.app.ecolive.payment_module.adapters.RecipentAdapterAdapter
import com.app.ecolive.user_module.FullImageActivity
import com.app.ecolive.utils.Utils

class RecipentListListActivity : AppCompatActivity() {
    lateinit var binding :ActivityRecipentListBinding
    lateinit var friendlistadapter : RecipentAdapterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_recipent_list)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text ="Select recipients"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.appCompatButton3.setOnClickListener {
            startActivity(Intent(this,SelectedRecipentActivity::class.java))
        }

        friendlistadapter = RecipentAdapterAdapter(this@RecipentListListActivity,object: RecipentAdapterAdapter.ClickListener{
            override fun onClick(pos: Int) {
//                startActivity(Intent(this@FriendsListActivity,ChatActivity::class.java))
            }

        })
        binding.recycleFriends.adapter =friendlistadapter
        binding.recycleFriends.layoutManager =LinearLayoutManager(this)
    }
}