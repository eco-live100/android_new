package com.app.ecolive.msg_module

import android.Manifest
import android.Manifest.permission.INTERNET
import android.Manifest.permission.RECORD_AUDIO
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityDialBinding
import com.app.ecolive.utils.AppConstant.MOCK_CALLER_NAME
import com.app.ecolive.utils.AppConstant.MOCK_CALLER_NUMBER
import com.app.ecolive.utils.AppConstant.MOCK_PASSWORD
import com.app.ecolive.utils.AppConstant.MOCK_USERNAME
import com.app.ecolive.utils.Utils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.telnyx.webrtc.sdk.CredentialConfig
import com.telnyx.webrtc.sdk.model.LogLevel
import com.telnyx.webrtc.sdk.model.SocketMethod
import com.telnyx.webrtc.sdk.verto.receive.AnswerResponse
import com.telnyx.webrtc.sdk.verto.receive.ByeResponse
import com.telnyx.webrtc.sdk.verto.receive.InviteResponse
import com.telnyx.webrtc.sdk.verto.receive.LoginResponse
import com.telnyx.webrtc.sdk.verto.receive.ReceivedMessageBody
import com.telnyx.webrtc.sdk.verto.receive.RingingResponse
import com.telnyx.webrtc.sdk.verto.receive.SocketObserver


import timber.log.Timber
import java.util.*

class VoipActivity : AppCompatActivity() {


    private var invitationSent: Boolean = false
    private lateinit var mainViewModel: MainViewModel


    lateinit var binding: ActivityDialBinding
    private var callId: UUID? = null
    var name = ""
    var mobile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dial)
        Utils.changeStatusColor(this, R.color.color_050D4C)
        mobile = intent.getStringExtra("mobile") ?: ""
        name = intent.getStringExtra("name") ?: "Eco live"

        mainViewModel = ViewModelProvider(this@VoipActivity)[MainViewModel::class.java]

        checkPermissions()
        initViews()
        binding.ivBack.setOnClickListener { finish() }
        binding.callInputId.setText(mobile.replace("\\s".toRegex(), ""))
       /* binding.CountryPicker.setOnCountryChangeListener {
            binding.callInputId.setText(binding.CountryPicker.selectedCountryCode.toString())
        }*/

    }


    private fun observeSocketResponses() {
        mainViewModel.getSocketResponse()
            ?.observe(
                this,
                object : SocketObserver<ReceivedMessageBody>() {
                    override fun onConnectionEstablished() {
                        doLogin()
                    }

                    override fun onMessageReceived(data: ReceivedMessageBody?) {
                        Timber.d("onMessageReceived from SDK [%s]", data?.method)
                       // toast("Message" + data?.method)
                        when (data?.method) {
                            SocketMethod.CLIENT_READY.methodName -> {
                                Timber.d("You are ready to make calls.")
                                binding.callButtonId.visibility = View.VISIBLE
                            }

                            SocketMethod.LOGIN.methodName -> {
                                binding.progressIndicatorId.visibility = View.INVISIBLE
                                val sessionId = (data.result as LoginResponse).sessid
                                Timber.d("Current Session: $sessionId")
                                onLoginSuccessfullyViews()
                            }

                            SocketMethod.RINGING.methodName -> {
                                var response = data.result as RingingResponse
                                callId = response.callId
                            }

                            SocketMethod.INVITE.methodName -> {
                                val inviteResponse = data.result as InviteResponse
                                /*onReceiveCallView(
                                    inviteResponse.callId,
                                    inviteResponse.callerIdNumber
                                )*/
                            }

                            SocketMethod.ANSWER.methodName -> {
                                val callId = (data.result as AnswerResponse).callId
                                launchCallInstance(callId)

                                binding.callButtonId.visibility = View.VISIBLE
                                binding.cancelCallButtonId.visibility =
                                    View.GONE
                                invitationSent = false
                            }

                            SocketMethod.BYE.methodName -> {
                                onByeReceivedViews()

                                val callId = (data.result as ByeResponse).callId
                                val callInstanceFragment = callInstanceFragments[callId]
                                callInstanceFragment?.let {
                                    supportFragmentManager.beginTransaction().remove(it).commit()
                                }

                            }
                        }
                    }

                    override fun onLoading() {
                        Timber.i("Loading...")
                    }

                    override fun onError(message: String?) {
                        Timber.e("onError: %s", message)
                        Toast.makeText(
                            this@VoipActivity,
                            message ?: "Socket Connection Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onSocketDisconnect() {
                        Toast.makeText(
                            this@VoipActivity,
                            "Socket is disconnected",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.progressIndicatorId.visibility = View.INVISIBLE

                        binding.callButtonId.visibility = View.GONE
                        binding.cancelCallButtonId.visibility = View.GONE
                        // binding.connectButtonId.visibility = View.VISIBLE

                        binding.status.text = getString(R.string.disconnected)
                        binding.callStateTextValue.text = "-"
                    }
                }
            )
    }


    private fun initViews() {
        binding.progressIndicatorId.visibility = View.VISIBLE

        mainViewModel.initConnection(applicationContext, null, null)
        observeSocketResponses()


        binding.callButtonId.setOnClickListener {
            mainViewModel.sendInvite(
                MOCK_CALLER_NAME,
                MOCK_CALLER_NUMBER,
                binding.callInputId.text.toString(),
                "Sample Client State"
            )
            binding.callButtonId.visibility = View.GONE
            binding.cancelCallButtonId.visibility = View.VISIBLE
        }
        binding.cancelCallButtonId.setOnClickListener {
            mainViewModel.endCall(callId)
            binding.callButtonId.visibility = View.VISIBLE
            binding.cancelCallButtonId.visibility = View.GONE
        }

    }


    private fun doLogin() {
        val loginConfig = CredentialConfig(
            MOCK_USERNAME,
            MOCK_PASSWORD,
            name,
            MOCK_CALLER_NUMBER,
            null,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), // or ringtone,
            R.raw.ringback_tone,
            LogLevel.ALL,

        )
        mainViewModel.doLoginWithCredentials(loginConfig)


    }


    private fun onLoginSuccessfullyViews() {
        binding.status.text = getString(R.string.connected)
        // binding.connectButtonId.visibility = View.GONE
        binding.callButtonId.visibility = View.VISIBLE

    }

    private val callInstanceFragments = mutableMapOf<UUID, CallInstanceFragment>()
    private fun launchCallInstance(callId: UUID) {
        mainViewModel.setCurrentCall(callId)
        val callInstanceFragment = CallInstanceFragment.newInstance(callId.toString())
        callInstanceFragments[callId] = callInstanceFragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_call_instance, callInstanceFragment)
            .commit()
    }

    private fun onByeReceivedViews() {
        invitationSent = false
        finish()
        binding.callButtonId.visibility = View.VISIBLE
        binding.cancelCallButtonId.visibility = View.GONE

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(
                    RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            // binding.connectButtonId.isClickable = true
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@VoipActivity,
                                "permissions are required to continue",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    RECORD_AUDIO,
                    INTERNET
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            //    binding.connectButtonId.isClickable = true
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@VoipActivity,
                                "permissions are required to continue",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.disconnect()
    }

}
