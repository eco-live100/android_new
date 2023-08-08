package com.app.ecolive.msg_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ChatlistActivityBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.msg_module.adapter.ChatListAdapter
import com.app.ecolive.rider_module.HomeRiderrActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.user_module.FullImageActivity
import com.app.ecolive.utils.PopUpVehicleChoose
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.cometchat.pro.models.Conversation

class ChatListActivity : AppCompatActivity() {

    lateinit var binding: ChatlistActivityBinding
    lateinit var chatListAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@ChatListActivity,R.layout.chatlist_activity)
        setToolBar()
        setListDummy()

        binding.chatCreateGrp.setOnClickListener {
            startActivity(Intent(this@ChatListActivity,CreateGrpActivity::class.java))
        }

        binding.toolbarChatList.deliveryCount.setOnClickListener{
            showPOPUP()
        }

    }


    private fun setListDummy() {
        val listRecentChat =ArrayList<Conversation>()
        cometchat.getRecentChat(object :CometChatInterface{
            override fun onRecentChat(list: List<Conversation>?) {
                for (i in list!!.indices){

                    listRecentChat.add(list[i])
                    chatListAdapter.notifyDataSetChanged()


                }
            }


        })





        chatListAdapter = ChatListAdapter(this@ChatListActivity,listRecentChat,object:ChatListAdapter.ClickListener{
            override fun onClick(id: String) {
                    startActivity(Intent(this@ChatListActivity,ChatActivity::class.java).putExtra("id",id))
            }

            override fun onClickImg(pos: Int) {
               // startActivity(Intent(this@ChatListActivity,FullImageActivity::class.java))
            }

        })

        binding.recycleChatList.also {
            it.layoutManager = LinearLayoutManager(this@ChatListActivity,LinearLayoutManager.VERTICAL,false)
            it.adapter = chatListAdapter
        }

    }

    private fun setToolBar() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)

        binding.toolbarChatList.toolbarTitle.text="Messaging"
        binding.toolbarChatList.cutmToolBarRightIcon2.visibility=View.INVISIBLE
        binding.toolbarChatList.ivUserImage.visibility=View.GONE
        binding.toolbarChatList.ivBack.visibility=View.VISIBLE
        binding.toolbarChatList.ivBack.setOnClickListener { finish() }
    }

    private fun showPOPUP() {
        PopUpVehicleChoose.getInstance().createDialog(
            this@ChatListActivity,
            "",
            object : PopUpVehicleChoose.Dialogclick {
                override fun onYes() {
                    riderLoginChk()
                }

                override fun onNo() {

                }

                override fun onPedesstrain() {

                }

                override fun onCycle() {

                }

                override fun onElectric() {

                }

                override fun onPetrol() {

                }

                override fun onBio() {

                }

            })
    }

    private fun riderLoginChk() {
        // startActivity(Intent(this@UserHomePageNavigationActivity, HomeRiderrActivity::class.java))

        if (PreferenceKeeper.instance.loginResponse != null) {
            if (PreferenceKeeper.instance.loginResponse!!.isRider) {
                // MyApp.popErrorMsg("","Your Vehicle details is in under verification",THIS!!)
                startActivity(
                    Intent(
                        this@ChatListActivity,
                        HomeRiderrActivity::class.java
                    )
                )
                finish()
            } else {
                startActivity(
                    Intent(
                        this@ChatListActivity,
                        VehicleInfoActivity::class.java
                    )
                )
            }
        } else {
            goLoginScreen()
        }
    }
    private fun goLoginScreen() {
        Utils.showMessage(this, resources.getString(R.string.you_login_first))
        startActivity(Intent(this@ChatListActivity, LoginActivity::class.java))
        finish()
    }
}