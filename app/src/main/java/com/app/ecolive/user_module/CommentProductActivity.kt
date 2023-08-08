package com.app.ecolive.user_module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.CommentproductActivityBinding
import com.app.ecolive.utils.Utils

class CommentProductActivity : AppCompatActivity() {
    lateinit var binding: CommentproductActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@CommentProductActivity,R.layout.commentproduct_activity)
        setToolbar()
    }

    private fun setToolbar() {
        binding.toolbarComment.toolbarTitle.text= "Comment"
        binding.toolbarComment.ivBack.setOnClickListener { finish() }
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }
}