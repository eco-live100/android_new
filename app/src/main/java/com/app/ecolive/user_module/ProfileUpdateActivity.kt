package com.app.ecolive.user_module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ProfileupdateActivityBinding
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide

class ProfileUpdateActivity : AppCompatActivity() {
    lateinit var binding : ProfileupdateActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this@ProfileUpdateActivity,R.layout.profileupdate_activity)

        setToolBar()

    }

    private fun setToolBar() {
        binding.toolbarProfileUpdate.toolbarTitle.text="Profile Update"
        binding.toolbarProfileUpdate.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)

        binding.profileUpdateName.setText(PreferenceKeeper.instance.loginResponse?.firstName)
        binding.profileUpdateEmail.text = PreferenceKeeper.instance.loginResponse?.email
        binding.profileUpdatePhno.text = PreferenceKeeper.instance.loginResponse?.mobileNumber

        Glide.with(this).load(PreferenceKeeper.instance.loginResponse?.profilePicture).into(binding.userProfileImage)
    }
}