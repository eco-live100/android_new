package com.app.ecolive.user_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ContactlistActivityBinding
import com.app.ecolive.msg_module.DialActivity
import com.app.ecolive.payment_module.model.Data
import com.app.ecolive.service.Status
import com.app.ecolive.user_module.model.UserModel
import com.app.ecolive.user_module.user_adapter.ContactListAdapter
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.PaymentViewModel
import com.nightout.ui.fragment.CallBottomSheet
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import org.json.JSONObject

class ContactListActivity : AppCompatActivity() {
    lateinit var binding: ContactlistActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ContactListActivity,R.layout.contactlist_activity)

       setToolBar()
        getUserListApiCall()
    }





    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbarContactList.toolbarTitle.text= "Contacts"
        binding.toolbarContactList.ivBack.setOnClickListener { finish() }
        binding.dialFab.setOnClickListener {
            startActivity(Intent(this,DialActivity::class.java))
        }
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
                            layoutManager = LinearLayoutManager(this@ContactListActivity)
                            adapter = ContactListAdapter(it.data,object : ContactListAdapter.ClickListener{
                                override fun onClick(
                                    data: Data,
                                    type: String,
                                    ivPhone: ZegoSendCallInvitationButton
                                ) {
                                    if(type=="voice"){
                                        initVoiceButton(ivPhone,data)

                                    }else{
                                        initVideoButton(ivPhone,data)
                                    }
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

    private fun initVideoButton(ivPhone: ZegoSendCallInvitationButton, data: Data) {


        ivPhone.setOnClickListener { v: View? ->

            val split: Array<String> =
                data._id.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val users: MutableList<ZegoUIKitUser> =
                java.util.ArrayList()
            for (userID in split) {
                users.add(ZegoUIKitUser(userID, data.firstName))
            }
            ivPhone.setInvitees(users)
        }
    }

    private fun initVoiceButton(ivPhone: ZegoSendCallInvitationButton, data: Data) {

        val newVoiceCall: ZegoSendCallInvitationButton = ivPhone
        newVoiceCall.setIsVideoCall(false)
        newVoiceCall.showErrorToast(false)


        newVoiceCall.resourceID = "Eco_live"
        newVoiceCall.setOnClickListener { v: View? ->
//            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
//            String targetUserID = inputLayout.getEditText().getText().toString();
            val split: Array<String> =
                data._id.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val users: MutableList<ZegoUIKitUser> =
                java.util.ArrayList()
            for (userID in split) {
                users.add(ZegoUIKitUser(userID, data.firstName))
            }
            newVoiceCall.setInvitees(users)
        }
    }
}