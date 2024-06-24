package com.app.ecolive.payment_module

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.PluginStub
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.databinding.ActivityNotifactionBinding
import com.app.ecolive.databinding.ActivityWebViewBinding
import com.app.ecolive.utils.Utils
import com.app.ecolive.utils.toast

class WebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebViewBinding
    var url =""
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_web_view)
        Utils.changeStatusColor(this, R.color.color_050D4C)

        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.toolbar.toolbarTitle.text ="Add money to wallet"
        url = (intent.getStringExtra("URL")?:"").toString()


        binding.webview.webViewClient = object : WebViewClient() {

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                Log.d("TAG", "shouldOverrideUrlLoading: $url")

                if (url != null) {
                    if (url.contains("Success",true)) {
                        toast("Money added successfully")
                        finish()
                    }
                }

                return true
            }

        }
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.userAgentString="Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]"
         binding.webview.loadUrl(url)


    }
}