package com.app.ecolive.login_module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.FragmentActivity;

import com.app.ecolive.R;
import com.app.ecolive.utils.AppConstant;


/**
 * @author Octa
 */
public class LoginToTwitter extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_twitter);

        Intent intent = getIntent();
        String mUrl = intent.getStringExtra(AppConstant.EXTRA_AUTH_URL_KEY);

        WebView webView = (WebView) findViewById(R.id.webViewLoginToTwitter);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new LoginToTwitterWebViewClient());

        webView.loadUrl(mUrl);
    }

    private class LoginToTwitterWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(getString(R.string.twitter_callback_url))) {
                Intent intent = new Intent();
                intent.putExtra(AppConstant.EXTRA_CALLBACK_URL_KEY, url);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            return false;
        }
    }
}