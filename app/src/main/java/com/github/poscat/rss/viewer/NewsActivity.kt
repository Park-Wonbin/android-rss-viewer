package com.github.poscat.rss.viewer

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class NewsActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private lateinit var mWebSettings: WebSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news)

        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        mWebView = findViewById(R.id.webView)
        mWebView.webViewClient = WebViewClient()
        mWebSettings = mWebView.settings
        mWebSettings.javaScriptEnabled = true

        var intent: Intent = intent
        mWebView.loadUrl(intent.getStringExtra("news_url"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}