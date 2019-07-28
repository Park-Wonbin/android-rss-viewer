package com.github.poscat.rss.viewer.activity

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.poscat.liveslider.LiveSliderAdapter
import com.github.poscat.liveslider.LiveSliderFeed
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.adapter.NewsPageAdapter
import com.github.poscat.rss.viewer.model.Channel
import com.github.poscat.rss.viewer.model.Items
import com.github.poscat.rss.viewer.model.News
import com.github.poscat.rss.viewer.utility.RetrofitAPI
import com.github.ybq.android.spinkit.style.Wave
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.feed.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class FeedActivity : AppCompatActivity() {

    // for Parsing
    private lateinit var mRetrofit: Retrofit
    private lateinit var mRetrofitAPI: RetrofitAPI
    private lateinit var mCallNewsList: Call<String>
    private lateinit var mGson: Gson

    // for RecyclerView
    private var mFeedAdapter: LiveSliderAdapter<Items>? = null

    // for Searching
    private var mOriginalData: Array<LiveSliderFeed<Items>>? = null

    // for Subscribe
    private var mSubscribeChannelId: String? = null
    private lateinit var pref: SharedPreferences
    private var channelIdList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)
        pref = getSharedPreferences("SUBSCRIBE", Activity.MODE_PRIVATE)

        statusBarSetting()
        recyclerViewSetting()
        progressBarSetting()
        uiSetting()

        retrofitBuilder()
        updateSubscribeList()
        getRSSData()
    }

    private fun getRSSData() {
        mCallNewsList = if (channelIdList.size > 0) mRetrofitAPI.getNewsList(*channelIdList.toTypedArray())
        else mRetrofitAPI.getNewsListAll()

        mCallNewsList.enqueue(mRetrofitCallback)
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

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mFeedAdapter!!.startAnimation((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
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

    private fun updateSubscribeList() {
        mSubscribeChannelId = pref.getString("channelId", "")
        if (mSubscribeChannelId != "") {
            channelIdList = mSubscribeChannelId.toString().split("/") as MutableList<String>
        }
    }

    private fun uiSetting() {
        // Refresher
        swipe_layout.setOnRefreshListener {
            getRSSData()
        }

        // Subscribe Button
        fab.setOnClickListener {
            // Get news channel list
            mCallNewsList = mRetrofitAPI.getChannelList()
            mCallNewsList.enqueue(mChannelCallback)
        }
    }

    private fun retrofitBuilder() {
        mGson = Gson()
        mRetrofit = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com").
            addConverterFactory(ScalarsConverterFactory.create()).build()
        mRetrofitAPI = mRetrofit.create(RetrofitAPI::class.java)
    }

    private fun searchFilter(str: String) {
        val word = str.toLowerCase()
        val newData = ArrayList<LiveSliderFeed<Items>>()

        if (mOriginalData != null)
            for (i in mOriginalData!!.iterator()) {
                val newItem = LiveSliderFeed<Items>()
                newItem.category = i.category
                newItem.items = ArrayList()

                if (i.items != null)
                    for (j in i.items!!.iterator()) {
                        // Check if the title or description contain the 'word'.
                        if(j.title != null && j.title!!.toLowerCase().contains(word))
                            newItem.items!!.add(j)
                        else if(j.description.toLowerCase().contains(word))
                            newItem.items!!.add(j)
                    }

                newData.add(newItem)
            }

        val array = Array(newData.size) { LiveSliderFeed<Items>() }
        mFeedAdapter!!.setData(newData.toArray(array))
    }

    private val mRetrofitCallback = object: Callback<String> {
        override fun onResponse(call:Call<String>, response: Response<String>) {
            swipe_layout.visibility = View.VISIBLE
            swipe_layout.isRefreshing = false
            progressBar.visibility = View.GONE

            val result = response.body()
            val listType = object : TypeToken<Array<News>>() {}.type
            val rawData = mGson.fromJson<Array<News>>(result, listType)
            val size = rawData?.size ?: 0
            val data = Array(size) { LiveSliderFeed<Items>() }

            if (rawData != null) {
                for ((idx, obj) in rawData.withIndex()) {
                    if (obj.title == null) {
                        data[idx].category = "Not yet updated"
                        continue
                    }
                    obj.items!!.sortByDescending { it.published }

                    data[idx].category = obj.title!!
                    data[idx].items = obj.items
                }
            }

            mFeedAdapter!!.setData(data)
            mOriginalData = data
        }

        override fun onFailure(call:Call<String>, t:Throwable) {
            swipe_layout.visibility = View.VISIBLE
            swipe_layout.isRefreshing = false
            progressBar.visibility = View.GONE

            t.printStackTrace()
        }
    }

    private val mChannelCallback = object: Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            val result = response.body()
            val listType = object : TypeToken<Array<Channel>>() {}.type
            val rawData = mGson.fromJson<Array<Channel>>(result, listType)

            val mUserItems: ArrayList<Int> = ArrayList()
            val listItems = Array(rawData.size) { "" }
            val listItemsId = Array(rawData.size) { "" }
            val checkedItems = BooleanArray(rawData!!.size)

            for ((idx, obj) in rawData.withIndex()) {
                listItems[idx] = obj.title ?: "Not yet updated"

                listItemsId[idx] = obj.id
                for (i in channelIdList) {
                    if (i == obj.id) {
                        checkedItems[idx] = true
                        mUserItems.add(idx)
                    }
                }
            }

            // Create Dialog for Subscribe
            val mBuilder = AlertDialog.Builder(this@FeedActivity)
            mBuilder.setTitle("보고싶은 채널을 구독해주세요.")
            mBuilder.setMultiChoiceItems(
                listItems, checkedItems
            ) { _, position, isChecked ->
                if (isChecked) {
                    if (!mUserItems.contains(position)) {
                        mUserItems.add(position)
                    }
                } else if (mUserItems.contains(position)) {
                    mUserItems.remove(position)
                }
            }

            mBuilder.setCancelable(false)
            mBuilder.setPositiveButton("완료") { _, _ ->
                var item = ""
                val editor = pref.edit()

                channelIdList = mutableListOf()
                for (i in 0 until mUserItems.size) {
                    channelIdList.add(listItemsId[mUserItems[i]])
                    item += listItemsId[mUserItems[i]]

                    if (i != mUserItems.size - 1) item += "/"
                }

                editor.putString("channelId", item)
                editor.commit()
                updateSubscribeList()

                progressBar.visibility = View.VISIBLE
                getRSSData()
            }

            mBuilder.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.dismiss() }

            val mDialog = mBuilder.create()
            mDialog.show()
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            t.printStackTrace()
        }
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
}