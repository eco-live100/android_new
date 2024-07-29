package com.app.ecolive.payment_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivitySendMoneyHomePageBinding
import com.app.ecolive.payment_module.adapters.UserListForAdapter
import com.app.ecolive.service.Status
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.Utils
import org.json.JSONObject
import com.app.ecolive.viewmodel.PaymentViewModel
import com.app.ecolive.payment_module.model.Data
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.toast
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig


class SendMoneyHomePageActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySendMoneyHomePageBinding
    val scanCustomCode = registerForActivityResult(ScanCustomCode(), ::handleResult)
    lateinit var userListForAdapter: UserListForAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@SendMoneyHomePageActivity,
            R.layout.activity_send_money_home_page
        )
        statusBarColor()
        setToolBar()
        binding.sendMoneyAddMoneyBtn.setOnClickListener(this)
        binding.sendMoneyScanQr.setOnClickListener(this)
        binding.ScanQrCode.setOnClickListener(this)
        binding.tvBalanceHistory.setOnClickListener(this)
    }

    private fun setToolBar() {

        binding.toolbarSendMoney.ivBack.setOnClickListener { finish() }
        binding.toolbarSendMoney.toolbarTitle.text = "Payment"
        binding.toolbarSendMoney.cutmToolBarRightIcon.visibility = GONE
        if (MyApp.isConnectingToInternet(this!!)) {
            getUserListApiCall()
            getWalletAmountApi()
        }

    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)
        userListForAdapter=  UserListForAdapter(
          arrayListOf(), arrayListOf(),
            object : UserListForAdapter.ClickListener {
                override fun onClick(pos: Data) {

                    if (PreferenceKeeper.instance.loginResponse?._id.equals(pos._id)){
                        toast("Can't make payment your self")
                        return
                    }
                    startActivity(
                        Intent(
                            this@SendMoneyHomePageActivity,
                            SendMoneyInternationLocl::class.java
                        ).putExtra("ID", pos._id).putExtra("EMAIL",pos.email)
                    )

                }

            })


        binding.contactListRecycle .apply {
            layoutManager = LinearLayoutManager(this@SendMoneyHomePageActivity)
            adapter = userListForAdapter
        }

        binding.search.doAfterTextChanged { userListForAdapter.filter.filter(it) }

    }

    override fun onStart() {
        super.onStart()
        getWalletAmountApi()
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.sendMoneyAddMoneyBtn -> {
                startActivity(
                    Intent(
                        this@SendMoneyHomePageActivity,
                        UserVerificationAddMoneyActivity::class.java
                    )
                )
            }

            binding.sendMoneyScanQr -> {
                startActivity(Intent(this@SendMoneyHomePageActivity, ScanQrActivity::class.java))
            }

            binding.ScanQrCode -> {
                scanCustomCode.launch(
                    ScannerConfig.build {
                        setBarcodeFormats(listOf(BarcodeFormat.FORMAT_ALL_FORMATS)) // set interested barcode formats
                        setOverlayStringRes(R.string.scan_barcode) // string resource used for the scanner overlay
                        setOverlayDrawableRes(R.drawable.app_logo_bgtrans) // drawable resource used for the scanner overlay
                        setHapticSuccessFeedback(false) // enable (default) or disable haptic feedback when a barcode was detected
                        setShowTorchToggle(true) // show or hide (default) torch/flashlight toggle button
                        setHorizontalFrameRatio(1.2f) // set the horizontal overlay ratio (default is 1 / square frame)
                        setUseFrontCamera(false) // use the front camera
                    }
                )
            }

            binding.tvBalanceHistory -> {
                startActivity(
                    Intent(
                        this@SendMoneyHomePageActivity,
                        TransactionHistoryActivity::class.java
                    )
                )
            }

        }
    }

    private fun getUserListApiCall() {
        binding.isShimmerShow = true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()

        paymentViewModel.getUserList(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        userListForAdapter.update(it.data)

                    }
                    binding.isShimmerShow = false

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

    private fun getWalletAmountApi() {
        //  binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()

        paymentViewModel.getWalletApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        AppConstant.walletAmount = it.data.money.toString()
                        AppConstant.walletCurrency = it.data.currency.toString()
                        "${it.data.money} ${it.data.currency}".also {
                            binding.walletAmount.text = "Wallet: " + it
                        }

                    }
                    //   binding.isShimmerShow =false

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


    fun handleResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                var id = result.content.rawValue
                if (PreferenceKeeper.instance.loginResponse?._id.equals(id)){
                    toast("Can't make payment your self")
                    return
                }
                startActivity(
                    Intent(
                        this@SendMoneyHomePageActivity,
                        SendMoneyInternationLocl::class.java
                    ).putExtra("ID",id)
                )
            }

            QRResult.QRUserCanceled -> "User canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
            else -> {}
        }


    }
}