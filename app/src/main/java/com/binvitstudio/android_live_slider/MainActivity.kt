package com.binvitstudio.android_live_slider

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.relex.circleindicator.CircleIndicator
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.page.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: PagerAdapter

    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500    // delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000  // time in milliseconds between successive task executions.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pagerAdapter = PagerAdapter(this)
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
}