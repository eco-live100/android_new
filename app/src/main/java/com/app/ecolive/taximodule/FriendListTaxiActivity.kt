package com.app.ecolive.taximodule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityFriendListPhramicyBinding
import com.app.ecolive.databinding.ActivityFriendTaxiPaymetnBinding
import com.app.ecolive.databinding.ActivityFriendsListBinding
import com.app.ecolive.msg_module.adapter.FriendListAdapter
import com.app.ecolive.utils.Utils

class FriendListTaxiActivity : AppCompatActivity() {
    lateinit var binding: ActivityFriendTaxiPaymetnBinding
    lateinit var friendlistadapter: FriendListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_taxi_paymetn)
        Utils.changeStatusColor(this, R.color.black)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text = "Payment request"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.appCompatButton3.setOnClickListener {
            //startActivity(Intent(this@FriendListTaxiActivity,PrescriptionRequestActivity::class.java))
        }

        friendlistadapter = FriendListAdapter(this@FriendListTaxiActivity,
            object : FriendListAdapter.ClickListener {
                override fun onClick(pos: Int) {
//                startActivity(Intent(this@FriendsListActivity,ChatActivity::class.java))
                }

            })
        binding.recycleFriends.adapter = friendlistadapter
        binding.recycleFriends.layoutManager = LinearLayoutManager(this)
    }
}