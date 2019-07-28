package com.github.poscat.rss.viewer.activity

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.github.poscat.rss.viewer.model.Item
import com.github.poscat.rss.viewer.utility.RetrofitAPI
import com.github.ybq.android.spinkit.style.Wave
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.feed.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class FeedActivity : AppCompatActivity() {

    // for Parsing
    private lateinit var mRetrofitAPI: RetrofitAPI
    private lateinit var mCallNewsList: Call<String>
    private lateinit var mGson: Gson

    // for RecyclerView
    private var mFeedAdapter: LiveSliderAdapter<Item>? = null

    // for Subscribe
    private var mSubscribeChannelId: String? = null
    private lateinit var pref: SharedPreferences
    private var mChannelList = arrayOf<Channel>()
    private var mSubscribedChannelList = mutableListOf<String>()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)
        pref = getSharedPreferences("SUBSCRIBE", Activity.MODE_PRIVATE)

        val provider = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)

        val observe = provider.getChannels()
            .flatMap {
                Observable.just(it)
            }.observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{}
            .doOnTerminate{}

        progressBarSetting()
        disposable.add(provider.getChannels()
            .flatMap {
                Observable.just(it)
            }.observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe{}
            .doOnTerminate{}
            .subscribe{
                swipe_layout.visibility = View.VISIBLE
                swipe_layout.isRefreshing = false
                progressBar.visibility = View.GONE
                Log.d("retrofit", it.size.toString())
            })

        /*
        statusBarSetting()
        recyclerViewSetting()
        progressBarSetting()
        uiSetting()

        retrofitBuilder()
        updateSubscribeList()
        getRSSData()
        */
    }

    private fun getRSSData() {
        mCallNewsList = if (mSubscribedChannelList.size > 0) mRetrofitAPI.getNewsList(*mSubscribedChannelList.toTypedArray())
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
            mSubscribedChannelList = mSubscribeChannelId.toString().split("/") as MutableList<String>
        }
    }

    private fun createChannelListSelector() : AlertDialog.Builder {
        val mBuilder = AlertDialog.Builder(this@FeedActivity)
        val mUserItems: ArrayList<Int> = ArrayList()
        val listItems = Array(mChannelList.size) { "" }
        val listItemsId = Array(mChannelList.size) { "" }
        val checkedItems = BooleanArray(mChannelList.size)

        for ((idx, obj) in mChannelList.withIndex()) {
            listItems[idx] = obj.title ?: getString(R.string.empty_content)
            listItemsId[idx] = obj.id

            for (i in mSubscribedChannelList) {
                if (i == obj.id) {
                    checkedItems[idx] = true
                    mUserItems.add(idx)
                }
            }
        }

        mBuilder.setTitle("보고싶은 채널을 구독해주세요.")
        mBuilder.setMultiChoiceItems(listItems, checkedItems) { _, position, isChecked ->
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

            mSubscribedChannelList = mutableListOf()
            for (i in 0 until mUserItems.size) {
                mSubscribedChannelList.add(listItemsId[mUserItems[i]])
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
        return mBuilder
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

    private fun retrofitBuilder() {
        val retrofit = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com").
            addConverterFactory(GsonConverterFactory.create()).build()
        mRetrofitAPI = retrofit.create(RetrofitAPI::class.java)
        mGson = Gson()
    }

    private fun searchFilter(str: String) {
        val word = str.toLowerCase()
        val newData = ArrayList<LiveSliderFeed<Item>>()

        if (mChannelList.isNotEmpty()) {
            for (channel in mChannelList.iterator()) {
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
        }

        val array = Array(newData.size) { LiveSliderFeed<Item>() }
        mFeedAdapter!!.setData(newData.toArray(array))
    }

    private val mRetrofitCallback = object: Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            val result = response.body()
            val listType = object : TypeToken<Array<Channel>>() {}.type
            mChannelList = mGson.fromJson(result, listType)

            val data = Array(mChannelList.size) { LiveSliderFeed<Item>() }

            if (mChannelList.isNotEmpty()) {
                for ((idx, obj) in mChannelList.withIndex()) {
                    if (obj.title == null) {
                        data[idx].category = getString(R.string.empty_content)
                        continue
                    }

                    obj.items?.sortByDescending { it.published }
                    data[idx].category = obj.title!!
                    data[idx].items = obj.items
                }
            }

            mFeedAdapter!!.setData(data)

            swipe_layout.visibility = View.VISIBLE
            swipe_layout.isRefreshing = false
            progressBar.visibility = View.GONE
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
            mChannelList = mGson.fromJson(result, listType)
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