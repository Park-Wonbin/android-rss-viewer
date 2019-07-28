package com.github.poscat.rss.viewer.activity

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.github.poscat.liveslider.LiveSliderAdapter
import com.github.poscat.liveslider.LiveSliderFeed
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.adapter.NewsPageAdapter
import com.github.poscat.rss.viewer.model.Channel
import com.github.poscat.rss.viewer.model.Item
import com.github.poscat.rss.viewer.utility.APIClient
import com.github.ybq.android.spinkit.style.Wave
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.feed.*

class FeedActivity : AppCompatActivity() {
    // for Parsing
    private val mAPIClient = APIClient()

    // for RecyclerView
    private var mFeedAdapter: LiveSliderAdapter<Item>? = null

    // for Subscribe
    private lateinit var pref: SharedPreferences
    private var mChannelList = arrayOf<Channel>()
    private var mSubscribeList = arrayOf<Channel>()
    private var mSubscribedChannelList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)
        pref = getSharedPreferences("SUBSCRIBE", Activity.MODE_PRIVATE)

        statusBarSetting()
        recyclerViewSetting()
        progressBarSetting()
        uiSetting()

        updateSubscribeList()
        getRSSData()
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

    private fun getRSSData() {
        val disposable = CompositeDisposable()
        val request = if (mSubscribedChannelList.isEmpty()) mAPIClient.getChannelsAPI()
        else mAPIClient.getSelectedChannelsAPI(mSubscribedChannelList.toTypedArray())

        disposable.add(request.subscribe({
                val data = Array(it.items.size) { LiveSliderFeed<Item>() }
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
                }

                mFeedAdapter!!.setData(data)
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

    private fun statusBarSetting() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.parseColor("#ffffff")
    }

    private fun recyclerViewSetting() {
        mFeedAdapter = LiveSliderAdapter(NewsPageAdapter(), true)
        mFeedAdapter!!.setHasStableIds(true)

        recycler_view.itemAnimator = null // Blink animation cancel(when data changed)
        recycler_view.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = mFeedAdapter
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) fab.hide()
                else fab.show()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                var animationItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (animationItemPosition == NO_POSITION) {
                        val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val lastItemPosition = layoutManager.findLastVisibleItemPosition()
                        val firstView = layoutManager.findViewByPosition(firstItemPosition)
                        val lastView = layoutManager.findViewByPosition(lastItemPosition)

                        animationItemPosition = when {
                            firstItemPosition == NO_POSITION -> lastItemPosition
                            lastItemPosition == NO_POSITION -> firstItemPosition
                            (firstView!!.bottom - recycler_view.top) >= (recycler_view.bottom - lastView!!.top) -> firstItemPosition
                            else -> lastItemPosition
                        }
                    }

                    mFeedAdapter!!.startAnimation(animationItemPosition)
                }
            }
        })
    }

    private fun progressBarSetting() {
        val wave = Wave()
        wave.color = getColor(R.color.colorAccent)

        progressBar.indeterminateDrawable = wave
        progressBar.visibility = View.VISIBLE
        swipe_layout.visibility = View.GONE
    }

    private fun uiSetting() {
        // Refresher
        swipe_layout.setOnRefreshListener {
            getRSSData()
        }

        // Subscribe Button
        fab.setOnClickListener {
            val mDialog = createChannelListSelector().create()
            mDialog.show()
        }
    }

    private fun updateSubscribeList() {
        val subscribedChannelIDs = pref.getString("channelId", "")
        if (subscribedChannelIDs != null && subscribedChannelIDs != "") {
            mSubscribedChannelList = subscribedChannelIDs.toString().split("/") as MutableList<String>
        }
    }

    private fun createChannelListSelector() : AlertDialog.Builder {
        val builder = AlertDialog.Builder(this@FeedActivity)
        val selectedChannels: ArrayList<Int> = ArrayList()
        val channelTitles = Array(mSubscribeList.size) { "" }
        val subscribedChannels = BooleanArray(mSubscribeList.size)

        for ((idx, obj) in mSubscribeList.withIndex()) {
            channelTitles[idx] = obj.title ?: getString(R.string.empty_content)

            for (i in mSubscribedChannelList) {
                if (i == obj.id) {
                    subscribedChannels[idx] = true
                    selectedChannels.add(idx)
                }
            }
        }

        builder.setTitle("보고싶은 채널을 구독해주세요.")
        builder.setMultiChoiceItems(channelTitles, subscribedChannels) { _, position, isChecked ->
            if (isChecked) {
                if (!selectedChannels.contains(position)) {
                    selectedChannels.add(position)
                }
            } else if (selectedChannels.contains(position)) {
                selectedChannels.remove(position)
            }
        }

        builder.setCancelable(false)
        builder.setPositiveButton("완료") { _, _ ->
            var item = ""
            val editor = pref.edit()

            mSubscribedChannelList = mutableListOf()
            for (i in 0 until selectedChannels.size) {
                mSubscribedChannelList.add(mSubscribeList[selectedChannels[i]].id)
                item += mSubscribeList[selectedChannels[i]].id

                if (i != selectedChannels.size - 1) item += "/"
            }

            editor.putString("channelId", item)
            editor.apply()
            updateSubscribeList()

            progressBar.visibility = View.VISIBLE
            getRSSData()
        }

        builder.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.dismiss() }
        return builder
    }

    private fun searchFilter(str: String) {
        val word = str.toLowerCase()
        val newData = ArrayList<LiveSliderFeed<Item>>()

        for (channel in mChannelList) {
            val newItem = LiveSliderFeed<Item>()
            newItem.category = channel.title ?: getString(R.string.empty_content)
            newItem.items = ArrayList()

            if (channel.items != null)
                for (item in channel.items!!.iterator()) {
                    // Check if the title or description contain the 'word'.
                    if (item.title != null && item.title!!.toLowerCase().contains(word))
                        newItem.items!!.add(item)
                    else if (item.description.toLowerCase().contains(word))
                        newItem.items!!.add(item)
                }

            newData.add(newItem)
        }


        val array = Array(newData.size) { LiveSliderFeed<Item>() }
        mFeedAdapter!!.setData(newData.toArray(array))
    }
}