package com.app.ecolive.msg_module


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ChatActivityBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.msg_module.adapter.ChatAdapter
import com.app.ecolive.rider_module.HomeRiderrActivity
import com.app.ecolive.rider_module.VehicleInfoActivity
import com.app.ecolive.utils.PopUpVehicleChoose
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.bumptech.glide.Glide
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec


class ChatActivity : AppCompatActivity() ,CometChatInterface{
    lateinit var binding: ChatActivityBinding
    var id: String = ""
    lateinit var chatAdapter: ChatAdapter
    var historyList = ArrayList<BaseMessage?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.chat_activity)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        Utils.changeStatusTextColor(this)
        binding.toolbarChat.ivBack.visibility = INVISIBLE
        binding.toolbarChat.ivUserImage.visibility = VISIBLE
        binding.toolbarChat.cutmToolBarRightIcon3.visibility = VISIBLE
        if (intent.extras != null) {
            id = intent.getStringExtra("id").toString()
        }

        val layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this, historyList)
        binding.recycleChat.layoutManager = layoutManager
        binding.recycleChat.adapter = chatAdapter

        cometchat.singleUserDetail(id, object : CometChatInterface {

            override fun getSingleUserDetails(user: User?) {
                setToolBar(user)
            }

        })




        binding.chatEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cometchat.onTypingStart(id)
            }

            override fun afterTextChanged(s: Editable?) {
                Handler(Looper.getMainLooper()).postDelayed({ cometchat.onTypingStop(id) },4000)

                if (!s.isNullOrEmpty()) {
                    binding.chatMicImg.setImageResource(android.R.drawable.ic_menu_send)
                } else {
                    binding.chatMicImg.setImageResource(R.drawable.ic_voice)

                }
            }
        })

        binding.chatMicImg.setOnClickListener {
            if (binding.chatEditText.text.toString().trim() != "") {
                cometchat.sendMessage(
                    id,
                    binding.chatEditText.text.trim().toString(),
                    object : CometChatInterface {
                        override fun onSendMessageSuccessfully(textMessage: TextMessage) {
                            historyList.add(textMessage)
                            chatAdapter.notifyDataSetChanged()
                            binding.chatEditText.setText("")
                            binding.recycleChat.scrollToPosition(chatAdapter.dataList.size - 1)
                        }
                    })

            }
        }

        cometchat.typingListner(object :CometChatInterface{
            override fun onTypingStart() {
                 binding.toolbarChat.typinglbl.visibility=View.VISIBLE
            }

            override fun onTypingStop() {
                binding.toolbarChat.typinglbl.visibility=View.INVISIBLE

            }
        })

    }

    private fun setToolBar(user: User?) {
        binding.toolbarChat.cutmToolBarRightIcon3.setOnClickListener {
            cometchat.initCall(user!!.uid)
            startActivity(Intent(this,CallActivity::class.java) )

        }
        cometchat.getChatHistory(id, object : CometChatInterface {
            override fun getChatHistory(list: List<BaseMessage?>) {
                historyList.addAll(list)
                chatAdapter.notifyDataSetChanged()
                binding.recycleChat.scrollToPosition(chatAdapter.dataList.size - 1)

            }
        })

        //binding.toolbarChat.ivBack.setOnClickListener { finish() }
        binding.toolbarChat.toolbarTitle.text = user!!.name ?: ""
        binding.toolbarChat.cutmToolBarRightIcon2.setImageResource(R.drawable.ic_call_white_top17)
        Glide.with(this).load(Uri.parse(user.avatar ?: "")).placeholder(R.drawable.user_icon_white)
            .into(binding.toolbarChat.ivUserImage)

        binding.toolbarChat.cutmToolBarRightIcon.setOnClickListener {
            showPopMenu(it)

        }

        binding.toolbarChat.deliveryCount.setOnClickListener {
            showPOPUP()
        }

    }

    private fun showPOPUP() {
        PopUpVehicleChoose.getInstance().createDialog(
            this@ChatActivity,
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
                        this@ChatActivity,
                        HomeRiderrActivity::class.java
                    )
                )
                finish()
            } else {
                startActivity(
                    Intent(
                        this@ChatActivity,
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
        startActivity(Intent(this@ChatActivity, LoginActivity::class.java))
        finish()
    }

    private fun showPopMenu(viewe: View?) {
        val balloon = Balloon.Builder(this@ChatActivity)
            .setWidthRatio(0.4f)
            .setLayout(R.layout.menu_popup_chat)
            .setWidth(BalloonSizeSpec.WRAP)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextColorResource(R.color.color_333333)
            .setTextSize(15f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(10)
            .setArrowPosition(0.5f)
            .setPadding(8)
            .setCornerRadius(5f)
            .setBackgroundColorResource(R.color.white)
            .setBalloonAnimation(BalloonAnimation.CIRCULAR)
            .build()
        if (balloon.isShowing) {
            balloon.dismiss()
        } else {
            balloon.showAsDropDown(viewe!!)
        }
        val menuViewContact: TextView = balloon.getContentView().findViewById(R.id.menuViewContact)
        val changeWallpaper: TextView = balloon.getContentView().findViewById(R.id.changeWallpaper)
        menuViewContact.setOnClickListener {
            Toast.makeText(this@ChatActivity, "ViewContact", Toast.LENGTH_SHORT).show()
            balloon.dismiss()
        }

        changeWallpaper.setOnClickListener {
            startActivity(Intent(this, WallpaperActivity::class.java))
            balloon.dismiss()
        }

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStart() {
        super.onStart()
        cometchat.messageReciver(object : CometChatInterface {
            override fun onCustomMessageReceived(customMessage: CustomMessage) {

            }

            override fun onMediaMessageReceived(mediaMessage: MediaMessage) {

            }

            override fun onTextMessageReceived(textMessage: TextMessage) {
                historyList.add(textMessage)
                chatAdapter.notifyDataSetChanged()
                binding.recycleChat.scrollToPosition(chatAdapter.dataList.size - 1)

            }
        })
    }

    override fun onPause() {
        super.onPause()
        cometchat.onMessageReciverStop()
    }

    fun startCall(sessionId: String) {
        val sessionID = sessionId
        var callView: RelativeLayout =binding.conslayout
        var activity: Activity

        val callSettings = CallSettings.CallSettingsBuilder(this, callView)
            .setSessionId(sessionID)
            .build()

        CometChat.startCall(callSettings, object : CometChat.OngoingCallListener {
            override fun onUserJoined(user: User) {
                Log.d("TAG", "onUserJoined: Name " + user.name)
            }

            override fun onUserLeft(user: User) {
                Log.d("TAG", "onUserLeft: " + user.name)
            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "onError: " + e.message)
            }

            override fun onCallEnded(call: Call) {
                Log.d("TAG", "onCallEnded: " + call.toString())
            }

            override fun onUserListUpdated(list: List<User>) {
                Log.d("TAG", "onUserListUpdated: $list")
            }

            override fun onAudioModesUpdated(list: List<AudioMode?>) {
                Log.d("TAG", "onAudioModesUpdated: $list")
            }

            override fun onRecordingStarted(p0: User?) {

            }

            override fun onRecordingStopped(p0: User?) {

            }

            override fun onUserMuted(p0: User?, p1: User?) {

            }

            override fun onCallSwitchedToVideo(p0: String?, p1: User?, p2: User?) {

            }
        })
    }

    override fun onStartCall(sessionId: String?) {
        if (sessionId != null) {
            startCall(sessionId)
        }
    }
}