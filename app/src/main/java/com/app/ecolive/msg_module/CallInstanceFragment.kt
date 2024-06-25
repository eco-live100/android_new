/*
 * Copyright © 2021 Telnyx LLC. All rights reserved.
 */

package com.app.ecolive.msg_module

import android.media.AudioManager
import android.media.ToneGenerator
import android.media.ToneGenerator.*
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityDialBinding
import com.app.ecolive.databinding.FragmentCallInstanceBinding
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.telnyx.webrtc.sdk.model.SocketMethod
import com.telnyx.webrtc.sdk.verto.receive.*
  import java.util.*


private const val CALLER_ID = "callId"

lateinit var mainViewModel: MainViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [CallInstanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallInstanceFragment : Fragment(R.layout.fragment_call_instance), NumberKeyboardListener {
    private var callId: UUID? = null

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    lateinit var binding2: ActivityDialBinding
    lateinit var  binding: FragmentCallInstanceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding2 =DataBindingUtil.setContentView(requireActivity(),R.layout.activity_dial)
        binding =DataBindingUtil.setContentView(requireActivity(),R.layout.fragment_call_instance)
        arguments?.let {
            callId = UUID.fromString(it.getString(CALLER_ID))
        }

        mainViewModel =
            ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setUpOngoingCallButtons()
        observeSocketResponses()
    }

    private fun setUpOngoingCallButtons() {

        //Handle call option observers
        mainViewModel.getCallState()?.observe(this.viewLifecycleOwner, { value ->
            binding2.callStateTextValue.text = value.name
        })
        mainViewModel.getIsMuteStatus()?.observe(this.viewLifecycleOwner, { value ->
            if (!value) {
                binding.muteButtonId.setImageResource(R.drawable.ic_mic_off)
            } else {
                binding.muteButtonId.setImageResource(R.drawable.ic_mic)
            }
        })

        mainViewModel.getIsOnHoldStatus()?.observe(this.viewLifecycleOwner, { value ->
            if (!value) {
                binding.holdButtonId.setImageResource(R.drawable.ic_hold)
            } else {
                binding.holdButtonId.setImageResource(R.drawable.ic_play)
            }
        })

        mainViewModel.getIsOnLoudSpeakerStatus()?.observe(this.viewLifecycleOwner, { value ->
            if (!value) {
                binding.loudSpeakerButtonId.setImageResource(R.drawable.ic_loud_speaker_off)
            } else {
                binding.loudSpeakerButtonId.setImageResource(R.drawable.ic_loud_speaker)
            }
        })

        onTimerStart()

        binding.endCallId.setOnClickListener {
            onEndCall()
        }
        binding.muteButtonId.setOnClickListener {
            mainViewModel.onMuteUnmutePressed()
        }
        binding.holdButtonId.setOnClickListener {
            mainViewModel.onHoldUnholdPressed(callId!!)
        }
        binding.loudSpeakerButtonId.setOnClickListener {
            mainViewModel.onLoudSpeakerPressed()
        }
        binding.dialPadButtonId.setOnClickListener {

        }
    }

    private fun onEndCall() {
        mainViewModel.endCall(callId!!)
        binding.callTimerId.stop()
        parentFragmentManager.beginTransaction().remove(this@CallInstanceFragment).commit();
        binding2.fragmentCallInstance.visibility =View.GONE
        requireActivity().finish()
    }

    private fun onTimerStart() {
        binding.callTimerId.base = SystemClock.elapsedRealtime()
        binding.callTimerId.start()
    }

    private fun observeSocketResponses() {
        mainViewModel.getSocketResponse()
            ?.observe(this.viewLifecycleOwner, object : SocketObserver<ReceivedMessageBody>() {
                override fun onMessageReceived(data: ReceivedMessageBody?) {
                    when (data?.method) {
                        SocketMethod.INVITE.methodName -> {
                            //NOOP
                        }
                        SocketMethod.BYE.methodName -> {

                        }
                    }
                }

                override fun onConnectionEstablished() {
                    //NOOP
                }

                override fun onLoading() {
                    //NOOP
                }

                override fun onError(message: String?) {
                    //NOOP
                }

                override fun onSocketDisconnect() {
                    //NOOP
                }

            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param callId
         * @return A new instance of fragment CallInstanceFragment.
         */
        @JvmStatic
        fun newInstance(callId: String) =
            CallInstanceFragment().apply {
                arguments = Bundle().apply {
                    putString(CALLER_ID, callId)
                }
            }
    }

    override fun onLeftAuxButtonClicked() {
        //NOOP
    }

    override fun onNumberClicked(number: Int) {
        mainViewModel.dtmfPressed(callId!!, number.toString())
        when (number) {
            0 -> {
                toneGenerator.startTone(TONE_DTMF_0, 500)
            }
            1 -> {
                toneGenerator.startTone(TONE_DTMF_1, 500)
            }
            2 -> {
                toneGenerator.startTone(TONE_DTMF_2, 500)
            }
            3 -> {
                toneGenerator.startTone(TONE_DTMF_3, 500)
            }
            4 -> {
                toneGenerator.startTone(TONE_DTMF_4, 500)
            }
            5 -> {
                toneGenerator.startTone(TONE_DTMF_5, 500)
            }
            6 -> {
                toneGenerator.startTone(TONE_DTMF_6, 500)
            }
            7 -> {
                toneGenerator.startTone(TONE_DTMF_7, 500)
            }
            8 -> {
                toneGenerator.startTone(TONE_DTMF_8, 500)
            }
            9 -> {
                toneGenerator.startTone(TONE_DTMF_9, 500)
            }
        }

    }

    override fun onRightAuxButtonClicked() {
        binding.dialpadSectionId.root.visibility = View.INVISIBLE
    }
}