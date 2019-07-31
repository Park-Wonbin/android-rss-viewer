package com.github.poscat.rss.viewer.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.adapter.RecentNewsAdapter
import com.github.poscat.rss.viewer.model.Channel
import com.github.poscat.rss.viewer.model.Item
import com.github.poscat.rss.viewer.utility.APIClient
import com.github.ybq.android.spinkit.style.Wave
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.feed.progressBar
import kotlinx.android.synthetic.main.recent_news.*
import kotlinx.android.synthetic.main.recent_news.recycler_view
import kotlinx.android.synthetic.main.recent_news.swipe_layout

class RecentNewsActivity : AppCompatActivity(), RecentNewsAdapter.ListOnClickListener {

    private var mAdapter: RecentNewsAdapter<RecentNewsActivity>? = null

    private val mAPIClient = APIClient()
    private var mChannelList = arrayOf<Channel>()
    private var mChannelId: Int = 0

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

        mChannelId = intent.getIntExtra("id", 0)

        swipe_layout.setOnRefreshListener {
            getRSSData(mChannelId.toString())
        }

        getRSSData(mChannelId.toString())
    }

    override fun openLink(link: String, title: String?) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchViewItem = menu.findItem(R.id.action_search)
        val searchViewAndroidActionBar = searchViewItem.actionView as SearchView
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewAndroidActionBar.clearFocus()
                searchFilter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchFilter(newText)

                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun progressBarSetting() {
        val wave = Wave()
        wave.color = getColor(R.color.colorAccent)

        progressBar.indeterminateDrawable = wave
        progressBar.visibility = View.VISIBLE
        swipe_layout.visibility = View.GONE
    }

    private fun getRSSData(id: String) {
        val disposable = CompositeDisposable()
        val request = mAPIClient.getSelectedChannelsAPI(Array(1) { id }, 100)

        disposable.add(request.subscribe({
            mChannelList = it.items.toTypedArray()
            mChannelList[0].items?.sortByDescending { item -> item.published }
            mAdapter?.setData(mChannelList[0].items)

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

    private fun searchFilter(str: String) {
        val word = str.toLowerCase()

        val newItem = ArrayList<Item>()
        if (mChannelList.isNotEmpty()) {
            for (item in mChannelList[0].items!!.iterator()) {
                // Check if the title or description contain the 'word'.
                if (item.title != null && item.title!!.toLowerCase().contains(word))
                    newItem.add(item)
                else if (item.description.toLowerCase().contains(word))
                    newItem.add(item)
            }
        }

        if (newItem.size == 0) no_result.visibility = View.VISIBLE
        else no_result.visibility = View.GONE

        mAdapter?.setData(newItem)
    }
}