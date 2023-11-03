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
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import com.app.ecolive.R
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/*@HiltAndroidApp*/
class MyApp : Application() {

    val appID:String="23292595a3bdb6aa"  // Replace with your App ID
    val region:String="us"  // Replace with your App Region ("eu" or "us")
    override fun onCreate() {
        super.onCreate()

        // init preference keeper
        PreferenceKeeper.setContext(applicationContext)
        application = this
        Companion.ctx = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        val appSettings = AppSettings.AppSettingsBuilder()
            .subscribePresenceForAllUsers()
            .setRegion(region)
            .autoEstablishSocketConnection(true)
            .build()

        CometChat.init(this,appID,appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                    Log.d("TAG", "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG", "Initialization failed with exception: " + p0?.message)
            }

        })



    }


    companion object {

        private lateinit var application: MyApp
        private lateinit var dialog: Dialog
        private var ctx: Context? = null
        var SHARED_PREF_NAME = "Brng_Pref"
        var locationLast:Location? =null
        var lastLocationAddress:String? =null
        fun getAppContext() : Context {
            return ctx!!
        }
        var myApp:MyApp?=null
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
