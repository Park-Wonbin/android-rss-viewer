package com.poapper.liveslider

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import me.relex.circleindicator.CircleIndicator
import java.util.*

class LiveSliderAdapter<T>(): RecyclerView.Adapter<LiveSliderAdapter<T>.LiveSliderViewHolder>() {
    private lateinit var pagerAdapter : LiveSliderPagerAdapter<T>
    private var delay : Long = 0
    private var period : Long = 0
    private var currentFeed = 0
    private var data : Array<Feed<T>>? = null

    constructor(pagerAdapter: LiveSliderPagerAdapter<T>) : this(pagerAdapter, 400, 400)

    constructor(pagerAdapter: LiveSliderPagerAdapter<T>, delay: Long, period: Long) : this() {
        this.pagerAdapter = pagerAdapter
        this.delay = delay
        this.period = period
    }

    override fun getItemId(position: Int) = data?.get(position).hashCode().toLong()

    override fun getItemCount() = data?.size ?: 0

    override fun onBindViewHolder(holder: LiveSliderViewHolder, position: Int) {
        val feed = data?.get(position)

        holder.category.text = feed?.category
        holder.currentPage = 0
        holder.timer?.cancel()

        if (currentFeed == position) {
            holder.setViewPager(feed, true)
            holder.setAutoSwipe()
        }
        else {
            holder.setViewPager(feed, false)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSliderViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.feed
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately)
        return LiveSliderViewHolder(view, context, pagerAdapter, delay, period)
    }

    fun startAnimation(position: Int) {
        if (position != -1 && currentFeed != position) {
            notifyItemRangeChanged(0, itemCount)
            currentFeed = position
        }
    }

    fun setData(data: Array<Feed<T>>?) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class LiveSliderViewHolder(v: View, private val context: Context, private val pagerAdapter: LiveSliderPagerAdapter<T>, private val delay: Long, private val period: Long) : RecyclerView.ViewHolder(v) {
        private val indicator: CircleIndicator = v.findViewById(R.id.indicator)
        private val viewPager: ViewPager = v.findViewById(R.id.viewPager)

        var currentPage = 0
        var timer : Timer? = null
        val category : TextView = v.findViewById(R.id.category)

        fun setViewPager(data: Feed<T>?, animation: Boolean) {
            pagerAdapter.setData(data, animation)

            viewPager.adapter = pagerAdapter
            indicator.setViewPager(viewPager)
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    currentPage = position
                    pagerAdapter.stopAnimation()
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }

        fun setAutoSwipe() { // Auto Swipe using Timer()
            val handler = Handler()
            val update = Runnable {
                if (currentPage == pagerAdapter.count) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
            }

            timer = Timer() // This will create a new Thread
            timer!!.schedule(object : TimerTask() { // task to be scheduled
                override fun run() {
                    handler.post(update)
                }
            }, delay, period)
        }
    }
}