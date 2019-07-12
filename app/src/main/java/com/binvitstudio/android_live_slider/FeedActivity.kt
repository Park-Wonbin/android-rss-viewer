package com.binvitstudio.android_live_slider

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import me.relex.circleindicator.CircleIndicator
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.page.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

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
        mFeedAdapter = FeedAdapter()
        mFeedAdapter!!.setHasStableIds(true)
        mRecyclerView!!.layoutManager = layoutManager
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.adapter = mFeedAdapter

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                Log.d("hihi", ""+newState + " - " + RecyclerView.SCROLL_STATE_IDLE + " "+(recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
                if (newState == RecyclerView.SCROLL_STATE_IDLE) mFeedAdapter!!.startAnimation((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
            }

        })

        // Retrofit ---
        mGson = Gson()
        mRetrofit = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com").addConverterFactory(ScalarsConverterFactory.create()).build()
        mRetrofitAPI = mRetrofit.create(RetrofitAPI::class.java)

        mCallNewsList = mRetrofitAPI.getNewsList()
        mCallNewsList.enqueue(mRetrofitCallback)
    }

    private val mRetrofitCallback = object: Callback<String> {
        override fun onResponse(call:Call<String>, response: Response<String>) {
            val result = response.body()
            Log.v("RetrofitCallback", result)
            val mNewsListVO = mGson.fromJson(result, Array<NewsListVO>::class.java)
            mFeedAdapter!!.setData(mNewsListVO)
        }
        override fun onFailure(call:Call<String>, t:Throwable) {
            t.printStackTrace()
        }
    }
}