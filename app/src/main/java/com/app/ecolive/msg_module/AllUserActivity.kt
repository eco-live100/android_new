package com.app.ecolive.msg_module

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAllUserBinding
import com.app.ecolive.payment_module.adapters.UserListForAdapter
import com.app.ecolive.payment_module.model.Data
import com.app.ecolive.service.Status
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.PaymentViewModel
import com.zegocloud.zimkit.common.ZIMKitRouter
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType
import org.json.JSONObject

class AllUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityAllUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@AllUserActivity,R.layout.activity_all_user)
        setToolBar()
        statusBarColor()
    }

    private fun setToolBar() {

        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.toolbar.toolbarTitle.text="User"
        binding.toolbar.cutmToolBarRightIcon.visibility= View.GONE
        if (MyApp.isConnectingToInternet(this!!)) {
            getUserListApiCall()
        }

    }
    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
    }

    private fun getUserListApiCall() {
        binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()

        paymentViewModel.getUserList(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        binding.contactListRecycle.apply {
                            layoutManager = LinearLayoutManager(this@AllUserActivity)
                            adapter = UserListForAdapter(it.data,object : UserListForAdapter.ClickListener{
                                override fun onClick(user: Data) {
                                    if(user._id.isNullOrEmpty()){
                                        return
                                    }
                                    startChat(
                                        user._id,
                                        ZIMKitConversationType.ZIMKitConversationTypePeer
                                    )

                                }

                            })
                        }

                    }
                    binding.isShimmerShow =false

                }
                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }
    private fun startChat(conversationID: String, type: ZIMKitConversationType) {
        ZIMKitRouter.toMessageActivity(this, conversationID, type)
        finish()
    }
}