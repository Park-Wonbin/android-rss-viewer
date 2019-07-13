package com.binvitstudio.android_live_slider

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import androidx.core.view.MenuItemCompat
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.feed.*
import com.github.ybq.android.spinkit.style.Wave


class FeedActivity : AppCompatActivity() {

    // for Parsing
    private lateinit var mRetrofit: Retrofit
    private lateinit var mRetrofitAPI: RetrofitAPI
    private lateinit var mCallNewsList: Call<String>
    private lateinit var mGson: Gson

    // for RecyclerView
    private var mRecyclerView: RecyclerView? = null
    private var mFeedAdapter: FeedAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)

        // RecyclerView Adapter ---
        var layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        mRecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView!!.itemAnimator = null // Blink animation cancel(when data changed)
        mFeedAdapter = FeedAdapter()
        mFeedAdapter!!.setHasStableIds(true)
        mRecyclerView!!.layoutManager = layoutManager
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.adapter = mFeedAdapter

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mFeedAdapter!!.startAnimation((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
            }

        })

        // ProgressBar
        val wave = Wave()
        wave.color = Color.parseColor("#26A69A")
        progressBar.indeterminateDrawable = wave
        progressBar.visibility = View.VISIBLE

        // Retrofit ---
        mGson = Gson()
        mRetrofit = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com").addConverterFactory(ScalarsConverterFactory.create()).build()
        mRetrofitAPI = mRetrofit.create(RetrofitAPI::class.java)

        mCallNewsList = mRetrofitAPI.getNewsList()
        mCallNewsList.enqueue(mRetrofitCallback)
    }

    private val mRetrofitCallback = object: Callback<String> {
        override fun onResponse(call:Call<String>, response: Response<String>) {
            progressBar.visibility = View.GONE

            val result = response.body()
            Log.v("RetrofitCallback", result)
            val mNewsListVO = mGson.fromJson(result, Array<NewsListVO>::class.java)
            mFeedAdapter!!.setData(mNewsListVO)
        }
        override fun onFailure(call:Call<String>, t:Throwable) {
            progressBar.visibility = View.GONE

            t.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchViewItem = menu.findItem(R.id.action_search)
        val searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewAndroidActionBar.clearFocus()
                SearchWord(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun SearchWord(word: String) {
        progressBar.visibility = View.VISIBLE
        mCallNewsList = mRetrofitAPI.getSearchList(word)
        mCallNewsList.enqueue(mRetrofitCallback)
    }
}