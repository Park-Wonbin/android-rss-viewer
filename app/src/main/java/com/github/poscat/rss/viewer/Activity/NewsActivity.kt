package com.github.poscat.rss.viewer.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.github.poscat.rss.viewer.R

class NewsActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private lateinit var mWebSettings: WebSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news)

        // Status Bar
        val viewMain: View = getWindow().getDecorView()
        if (viewMain != null) {
            viewMain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            getWindow().setStatusBarColor(Color.parseColor("#ffffff"))
        }

        mWebView = findViewById(R.id.webView)
        mWebView.webViewClient = WebViewClient()
        mWebSettings = mWebView.settings
        mWebSettings.javaScriptEnabled = true

        var intent: Intent = intent
        mWebView.loadUrl(intent.getStringExtra("news_url"))
    }
}