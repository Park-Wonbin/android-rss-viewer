package com.binvitstudio.android_live_slider

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.relex.circleindicator.CircleIndicator
import androidx.viewpager.widget.ViewPager
import com.github.ybq.android.spinkit.style.Wave
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.progressBar
import kotlinx.android.synthetic.main.feed.*
import kotlinx.android.synthetic.main.page.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {

    // for Parsing
    private lateinit var mRetrofit: Retrofit
    private lateinit var mRetrofitAPI: RetrofitAPI
    private lateinit var mCallNewsList: Call<String>
    private lateinit var mGson: Gson

    // for View Pager
    private lateinit var pagerAdapter: PagerAdapter

    // for Auto Swipe
    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 4000    // delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 4000  // time in milliseconds between successive task executions.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        button.setOnClickListener {
            intent = Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setViewPager(data: NewsListVO) {  // View Page
        pagerAdapter = PagerAdapter(this)
        pagerAdapter.setNewsData(data, true)
        viewPager.adapter = pagerAdapter

        val indicator = findViewById(R.id.indicator) as CircleIndicator
        indicator.setViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
                viewPager.description.clearAnimation()
                viewPager.description.visibility = View.INVISIBLE
                pagerAdapter.stopAnim()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        /* After setting the adapter use the timer */
        setAutoSwipe()
    }

    private fun setAutoSwipe() { // Auto Swipe using Timer()
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == pagerAdapter.count) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer() // This will create a new Thread
        timer!!.schedule(object : TimerTask() { // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    private val mRetrofitCallback = object: Callback<String> {
        override fun onResponse(call:Call<String>, response: Response<String>) {
            progressBar.visibility = View.GONE

            val result = response.body()
            Log.v("RetrofitCallback", result)
            val mNewsListVO = mGson.fromJson(result, Array<NewsListVO>::class.java)
            setViewPager(mNewsListVO[0])
        }
        override fun onFailure(call:Call<String>, t:Throwable) {
            progressBar.visibility = View.GONE

            t.printStackTrace()
        }
    }
}