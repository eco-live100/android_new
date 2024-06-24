package com.app.ecolive.common_screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.ecolive.R
import com.app.ecolive.login_module.LocationPickerActivity
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils.Companion.changeStatusColor
import com.app.ecolive.utils.Utils.Companion.changeStatusTextColor
import com.google.gson.Gson
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarColor()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startSplash()
    }

    private fun statusBarColor() {
        changeStatusColor(this, R.color.white)
        changeStatusTextColor(this)

    }

    private fun startSplash() {
        try {
            Handler(Looper.getMainLooper()).postDelayed({

           val lastLocationLat=     PreferenceKeeper.instance.lastLocationLat
           var lastLocationLang=     PreferenceKeeper.instance.lastLocationLang
                        if(PreferenceKeeper.instance.isUserLogin){
                            if (lastLocationLat ==""){
                                startActivity(Intent(this@SplashActivity, LocationPickerActivity::class.java))
                            }else{
                                val temp = Location(LocationManager.GPS_PROVIDER)
                                temp.latitude = lastLocationLat!!.toDouble()
                                temp.longitude =lastLocationLang!!.toDouble()
                                MyApp.locationLast =temp
                                MyApp.lastLocationAddress = PreferenceKeeper.instance.lastAddress
                                MyApp.lastLocationAddresstitle = PreferenceKeeper.instance.lastAddressTitle
                                startActivity(Intent(this@SplashActivity, UserHomePageNavigationActivity::class.java))

                            }

                        }else{
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        }


                finish()
                }, 1500)

        }catch (e:Exception){
           // Log.d("TAG", "startSplash: "+e)
        }
    }


    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager()
                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("ok", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("ok", "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e("ok", "printHashKey()", e)
        }
    }

}