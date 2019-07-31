package com.github.poscat.rss.viewer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
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

    private lateinit var mTitle: String
    private lateinit var mLink: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news)

        // Status Bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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

        mLink = intent.getStringExtra("news_url")
        mTitle = intent.getStringExtra("news_title")
        mWebView.loadUrl(mLink)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "$mTitle - $mLink")
                val chooser = Intent.createChooser(intent, "링크 공유하기")
                startActivity(chooser)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.share, menu)

        return super.onCreateOptionsMenu(menu)
    }
}