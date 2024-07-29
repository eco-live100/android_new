package com.app.ecolive.msg_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityAllUserBinding
import com.app.ecolive.payment_module.SendMoneyInternationLocl
import com.app.ecolive.payment_module.adapters.UserListForAdapter
import com.app.ecolive.payment_module.model.Data
import com.app.ecolive.service.Status
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.toast
import com.app.ecolive.viewmodel.PaymentViewModel
import com.zegocloud.zimkit.common.ZIMKitRouter
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType
import org.json.JSONObject

class AllUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityAllUserBinding
    lateinit var userListForAdapter: UserListForAdapter

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
        userListForAdapter=  UserListForAdapter(
            arrayListOf(), arrayListOf(),
            object : UserListForAdapter.ClickListener {
                override fun onClick(user: Data) {

                    if (PreferenceKeeper.instance.loginResponse?._id.equals(user._id)){
                        toast("Can't make payment your self")
                        return
                    }
                    if(user._id.isNullOrEmpty()){
                        return
                    }
                    startChat(
                        user._id,
                        ZIMKitConversationType.ZIMKitConversationTypePeer
                    )

                }

            })


        binding.contactListRecycle.apply {
            layoutManager = LinearLayoutManager(this@AllUserActivity)
            adapter = userListForAdapter
        }

        binding.search.doAfterTextChanged { userListForAdapter.filter.filter(it) }
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
                        userListForAdapter.update(it.data)


                    }
                    binding.isShimmerShow =false

                }
                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, this)
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