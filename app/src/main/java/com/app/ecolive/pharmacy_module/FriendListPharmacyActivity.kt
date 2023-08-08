package com.app.ecolive.pharmacy_module

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityFriendListPhramicyBinding
import com.app.ecolive.databinding.ActivityFriendsListBinding
import com.app.ecolive.msg_module.adapter.FriendListAdapter
import com.app.ecolive.utils.Utils

class FriendListPharmacyActivity : AppCompatActivity() {
    lateinit var binding: ActivityFriendListPhramicyBinding
    lateinit var friendlistadapter: FriendListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_list_phramicy)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbar.toolbarTitle.text = "Friend list(Eco-Live)"
        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }
        binding.appCompatButton3.setOnClickListener {
            startActivity(Intent(this@FriendListPharmacyActivity,PrescriptionRequestActivity::class.java))
        }

        friendlistadapter = FriendListAdapter(this@FriendListPharmacyActivity,
            object : FriendListAdapter.ClickListener {
                override fun onClick(pos: Int) {
//                startActivity(Intent(this@FriendsListActivity,ChatActivity::class.java))
                }

            })
        binding.recycleFriends.adapter = friendlistadapter
        binding.recycleFriends.layoutManager = LinearLayoutManager(this)
    }
}