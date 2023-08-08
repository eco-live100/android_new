package com.app.ecolive.user_module

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.FullimageActivityBinding
import com.app.ecolive.utils.PreferenceKeeper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class FullImageActivity : AppCompatActivity() {
    lateinit var binding: FullimageActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@FullImageActivity,R.layout.fullimage_activity)
        binding.fullImageBackBtn.setOnClickListener { finish() }
        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isVendor||PreferenceKeeper.instance.loginResponse!!.isRider) {
                binding.ratingbar.visibility=View.VISIBLE
                binding.buttonrating.visibility=View.VISIBLE
            }else{
                binding.ratingbar.visibility=View.GONE
                binding.buttonrating.visibility=View.GONE
            }
        }

        Glide.with(this@FullImageActivity).load(R.drawable.dummy_male_user).listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                binding.progressBar.visibility = View.GONE
                return false
            }
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                binding.progressBar.visibility = View.GONE
                return false
            }
        }).into(binding.imgZoomView)

    }
}