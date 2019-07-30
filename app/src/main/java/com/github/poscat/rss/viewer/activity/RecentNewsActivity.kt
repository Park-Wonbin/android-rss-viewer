package com.github.poscat.rss.viewer.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.poscat.liveslider.LiveSliderFeed
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.adapter.RecentNewsAdapter
import com.github.poscat.rss.viewer.model.Channel
import com.github.poscat.rss.viewer.model.Item
import com.github.poscat.rss.viewer.utility.APIClient
import com.github.ybq.android.spinkit.style.Wave
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.feed.*
import kotlinx.android.synthetic.main.recent_news.recycler_view
import kotlinx.android.synthetic.main.recent_news.swipe_layout

class RecentNewsActivity : AppCompatActivity(), RecentNewsAdapter.listOnClickListener {

    private var mAdapter: RecentNewsAdapter<RecentNewsActivity>? = null

    private lateinit var pref: SharedPreferences
    private val mAPIClient = APIClient()
    private var mChannelList = arrayOf<Channel>()
    private var mSubscribeList = arrayOf<Channel>()
    private var mSubscribedChannelList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recent_news)

        // Status Bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.parseColor("#ffffff")

        // Toolbar
        supportActionBar?.title = intent.getStringExtra("title")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBarSetting()

        // RecyclerView
        mAdapter = RecentNewsAdapter(this)
        recycler_view.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = mAdapter

        swipe_layout.setOnRefreshListener {
            getRSSData()
        }

        // --- 여기부터 데이터 불러와서 넣어주세요 ---
        pref = getSharedPreferences("SUBSCRIBE", Activity.MODE_PRIVATE)

        getRSSData()
    }

    override fun gotoLink(link: String, title: String?) {
        val intent = Intent(applicationContext, NewsActivity::class.java)
        intent.putExtra("news_title", title)
        intent.putExtra("news_url", link)
        startActivity(intent)
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

    private fun progressBarSetting() {
        val wave = Wave()
        wave.color = getColor(R.color.colorAccent)

        progressBar.indeterminateDrawable = wave
        progressBar.visibility = View.VISIBLE
        swipe_layout.visibility = View.GONE
    }

    private fun getRSSData() {
        val disposable = CompositeDisposable()
        val id = intent.getIntExtra("id", 0)
        val request = mAPIClient.getSelectedChannelsAPI(Array(1) { id.toString() })

        disposable.add(request.subscribe({
            val data = Array(it.items.size) { LiveSliderFeed<Item, Int>() }
            mChannelList = it.items.toTypedArray()
            mSubscribeList = it.channels.toTypedArray()

            for ((idx, obj) in mChannelList.withIndex()) {
                if (obj.title == null) {
                    data[idx].category = getString(R.string.empty_content)
                    continue
                }

                obj.items?.sortByDescending { it.published }
                data[idx].category = obj.title!!
                data[idx].items = obj.items

                mAdapter?.setData(obj.items)

                break // 하나만 넣기
            }

            swipe_layout.visibility = View.VISIBLE
            swipe_layout.isRefreshing = false
            progressBar.visibility = View.GONE
        }, { error ->
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            swipe_layout.visibility = View.VISIBLE
            swipe_layout.isRefreshing = false
            progressBar.visibility = View.GONE
        }))
    }

    private fun updateSubscribeList() {
        val subscribedChannelIDs = pref.getString("channelId", "")
        if (subscribedChannelIDs != null && subscribedChannelIDs != "") {
            mSubscribedChannelList = subscribedChannelIDs.toString().split("/") as MutableList<String>
        }
    }
}