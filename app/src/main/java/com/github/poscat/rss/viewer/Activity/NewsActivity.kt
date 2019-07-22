package com.github.poscat.rss.viewer.Activity

import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
<<<<<<< Updated upstream:app/src/main/java/com/github/poscat/rss/viewer/NewsActivity.kt
import com.github.poscat.R
=======
import com.github.poscat.rss.viewer.R
>>>>>>> Stashed changes:app/src/main/java/com/github/poscat/rss/viewer/Activity/NewsActivity.kt

class NewsActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private lateinit var mWebSettings: WebSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news)

        mWebView = findViewById(R.id.webView)
        mWebView.webViewClient = WebViewClient()
        mWebSettings = mWebView.settings
        mWebSettings.javaScriptEnabled = true

        var intent: Intent = intent
        mWebView.loadUrl(intent.getStringExtra("news_url"))
    }
}