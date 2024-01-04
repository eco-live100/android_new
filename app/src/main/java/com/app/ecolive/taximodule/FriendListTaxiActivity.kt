package com.app.ecolive.taximodule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityFriendTaxiPaymetnBinding
import com.app.ecolive.pharmacy_module.adapter.DoctorListAdapter
import com.app.ecolive.utils.Utils

class FriendListTaxiActivity : AppCompatActivity() {
    lateinit var binding: ActivityFriendTaxiPaymetnBinding
    lateinit var friendlistadapter: DoctorListAdapter
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

/*        friendlistadapter = FriendListAdapter(this@FriendListTaxiActivity,
            object : FriendListAdapter.ClickListener {
                override fun onClick(pos: Int) {
//                startActivity(Intent(this@FriendsListActivity,ChatActivity::class.java))
                }

            })
        binding.recycleFriends.adapter = friendlistadapter
        binding.recycleFriends.layoutManager = LinearLayoutManager(this)*/
    }
}