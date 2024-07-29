package com.app.ecolive.utils


import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.ecolive.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zegocloud.zimkit.services.ZIMKit
import com.zegocloud.zimkit.services.config.InputConfig


/*@HiltAndroidApp*/
class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        val appLifecycleObserver = AppLifecycleObserver()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        // init preference keeper
        PreferenceKeeper.setContext(applicationContext)
        application = this
        Companion.ctx = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        ZIMKit.initWith(this, KeyCenter.APP_ID2, KeyCenter.APP_SIGN2)
        ZIMKit.initNotifications()

        val inputConfig = InputConfig()
        inputConfig.showVoiceButton = true
        inputConfig.showEmojiButton = true
        inputConfig.showAddButton = true
        ZIMKit.setInputConfig(inputConfig)

    }


    companion object {

        private lateinit var application: MyApp
        private lateinit var dialog: Dialog
        private var ctx: Context? = null
        var SHARED_PREF_NAME = "Brng_Pref"
        var locationLast: Location? = null ////ik*****************
        var lastLocationAddress: String? = null///**********
        var lastLocationAddresstitle: String? = null///**********
        var driverlocation: Location? = null
        var driverLocationAddress: String? = null
        fun getAppContext(): Context {
            return ctx!!
        }

        var myApp: MyApp? = null
        fun hideSoftKeyboard(activity: Activity) {
            try {
                val inputMethodManager: InputMethodManager = activity
                    .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
            }
        }


        fun preventDoubleClick(view: View) {
            view.isClickable = false
            view.postDelayed({ view.isClickable = true }, 1000)
        }


        fun popErrorMsg(titleMsg: String, errorMsg: String, context: Context) {
            // pop error message
            val builder = MaterialAlertDialogBuilder(context, R.style.Theme_MyApp_Dialog_Alert)
            // val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(titleMsg).setMessage(errorMsg)
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }

            val alert = builder.create()
            alert.show()
        }

        fun popErrorMsg2(
            titleMsg: String,
            errorMsg: String,
            context: Context,
            callback: (isclick: Boolean) -> Unit
        ) {
            // pop error message
            val builder = MaterialAlertDialogBuilder(context, R.style.Theme_MyApp_Dialog_Alert)
            // val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(titleMsg).setMessage(errorMsg)


                .setPositiveButton("OK") { dialog, which ->
                    callback.invoke(true)
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.setCancelable(false)
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }

        fun ShowMassage(ctx: Context, msg: String) {
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle(null).setMessage(msg)
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }

            val alert = builder.create()
            alert.show()
        }


        fun isValidEmail(target: CharSequence): Boolean {
            return if (target == null) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches()
            }
        }

        @JvmStatic
        fun isConnectingToInternet(context: Context): Boolean {
            var connected = false
            val connectivity = context
                .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.activeNetworkInfo
            connected = info != null && info.isConnected && info.isAvailable
            return connected
        }


    }


}
