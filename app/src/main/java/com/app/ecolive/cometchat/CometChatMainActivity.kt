package com.app.ecolive.cometchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.app.ecolive.R
import com.app.ecolive.cometchat.Fragments.CallFragment
import com.app.ecolive.cometchat.Fragments.ChatFragment
import com.app.ecolive.cometchat.Fragments.UserFragment
import com.app.ecolive.cometchat.adapter.ViewPagerAdapter
import com.app.ecolive.databinding.ActivityCometChatMainBinding
import com.app.ecolive.utils.Utils
import com.google.android.material.tabs.TabLayout

class CometChatMainActivity : AppCompatActivity() {
    lateinit var bin: ActivityCometChatMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = DataBindingUtil.setContentView(this, R.layout.activity_comet_chat_main)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        setSupportActionBar(bin.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val adapter = ViewPagerAdapter(supportFragmentManager)

        // add fragment to the list
        adapter.addFragment(ChatFragment(), "Chat")
//        adapter.addFragment(CallFragment(), "Call")
        adapter.addFragment(UserFragment(), "User")

        // Adding the Adapter to the ViewPager
        bin.viewPager.adapter = adapter
        // bind the viewPager with the TabLayout.
        bin.tabs.setupWithViewPager(bin.viewPager)
        bin.viewPager.offscreenPageLimit =2

        listner()
    }

    private fun listner() {
         bin.ivBack.setOnClickListener { finish() }
    }
}