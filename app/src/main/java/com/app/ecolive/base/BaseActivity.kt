package com.offercity.base

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.TouchEffect
import com.localmerchants.utils.ExceptionHandler


open class BaseActivity:AppCompatActivity(),View.OnClickListener {
    val TOUCH = TouchEffect()
    var THIS: BaseActivity? = null
    private var exceptionHandler: ExceptionHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        THIS = this
        exceptionHandler = ExceptionHandler(this)
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)



        //status bar full screen
//        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
//            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
//        }
//        if (Build.VERSION.SDK_INT >= 19) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
//            window.statusBarColor = Color.TRANSPARENT
//        }
    }


    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
      /*fun parseStringData(data: String): RequestBody? {
        return RequestBody.create(MediaType.parse("text/plain"), data)
    }*/
    fun noNetConnection() {
       // MyApp.popErrorMsg("", resources.getString(R.string.No_Internet), THIS!!)
    }

    override fun onClick(v: View?) {

    }

    fun setTouchNClick(id: Int): View? {
        val v:View?=setClick(id)
        v?.setOnTouchListener(TOUCH)
        return v

    }


     fun setClick(id: Int): View {
         val v:View = findViewById(id)
         v.setOnClickListener(this)
         return v
    }
    fun setClick(v: View?): View? {


        v?.setOnClickListener(this)
        return v
    }
    fun setTouchNClick(v: View?): View? {
        setClick(v)
        v?.setOnTouchListener(TOUCH)
        return v
    }




}