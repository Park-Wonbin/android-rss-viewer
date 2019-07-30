package com.github.poscat.rss.viewer.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.github.poscat.rss.viewer.R
import kotlinx.android.synthetic.main.news.*

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

        // Toolbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // WebView
        mWebView = findViewById(R.id.webView)
        mWebView.webViewClient = WebViewClient()
        mWebSettings = mWebView.settings
        mWebSettings.javaScriptEnabled = true
        mWebView.webViewClient = object:WebViewClient() {
            override fun shouldOverrideUrlLoading(view:WebView?, url:String?):Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view:WebView?, url:String?, favicon:android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view:WebView?, url:String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.INVISIBLE
            }
        }

        mWebView.loadUrl(intent.getStringExtra("news_url"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}