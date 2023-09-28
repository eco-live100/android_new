package com.app.ecolive.msg_module

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityCallBinding
import com.cometchat.pro.core.Call
import com.cometchat.pro.core.CallSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.AudioMode
import com.cometchat.pro.models.User

class CallActivity : AppCompatActivity() ,CometChatInterface {
    lateinit var binding:  ActivityCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_call)
        cometchat.onInstance(this)

    }

    fun startCall(sessionId: String) {
        val sessionID = sessionId
        var callView: RelativeLayout =binding.conslayout
        var activity: Activity
        val relativeLayout = RelativeLayout(this)

        val relativeParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        )
        relativeLayout.layoutParams =relativeParams


        val callSettings = CallSettings.CallSettingsBuilder(this, callView)
            .setSessionId(sessionID)
            .showEndCallButton(true)
            .showMuteAudioButton(true)
            .build()

        CometChat.startCall(callSettings, object : CometChat.OngoingCallListener {
            override fun onUserJoined(user: User) {
                Log.d("TAG", "onUserJoined: Name " + user.name)
            }

            override fun onUserLeft(user: User) {
                Log.d("TAG", "onUserLeft: " + user.name)
                finish()
            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "onError: " + e.message)
                finish()
            }

            override fun onCallEnded(call: Call) {
                Log.d("TAG", "onCallEnded: " + call.toString())
                finish()
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