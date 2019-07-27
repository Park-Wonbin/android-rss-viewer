package com.github.poscat.rss.viewer.activity

import android.annotation.SuppressLint
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news)

        // Status Bar
        val viewMain: View = window.decorView
        viewMain.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.parseColor("#ffffff")

        mWebView = findViewById(R.id.webView)
        mWebView.webViewClient = WebViewClient()
        mWebSettings = mWebView.settings
        mWebSettings.javaScriptEnabled = true

        mWebView.loadUrl(intent.getStringExtra("news_url"))
    }
}