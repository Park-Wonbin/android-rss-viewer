package com.binvitstudio.android_live_slider

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.page.view.*
import me.relex.circleindicator.CircleIndicator
import java.util.*

class FeedAdapter(): RecyclerView.Adapter<FeedAdapter.listAdapterViewHolder>() {

    private var currentCategory: Int = 0

    private var mData: Array<NewsListVO>? = null

    override fun getItemId(position: Int): Long {
        Log.d("hashcode", mData!![position].hashCode().toLong().toString())
        return mData!![position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: listAdapterViewHolder, position: Int) {
        val thisItem = mData!![position]

        holder.category.text = thisItem.title
        holder.currentPage = 0
        if (holder.timer != null) {
            holder.timer!!.cancel()
        }

        if (currentCategory == position) {
            holder.setViewPager(thisItem, true)
            /* After setting the adapter use the timer */
            holder.setAutoSwipe()
        }
        else {
            holder.setViewPager(thisItem, false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): listAdapterViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.card
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately)
        return listAdapterViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    inner class listAdapterViewHolder(v: View, context: Context) : RecyclerView.ViewHolder(v) {

        private var context= context

        // for View Pager
        private lateinit var pagerAdapter: PagerAdapter
        private var viewPager: ViewPager = v.findViewById(R.id.viewPager)
        private val indicator: CircleIndicator = v.findViewById(R.id.indicator)

        // for Auto Swipe
        var currentPage = 0
        var timer: Timer? = null
        private val DELAY_MS: Long = 4000    // delay in milliseconds before task is to be executed
        private val PERIOD_MS: Long = 4000  // time in milliseconds between successive task executions.

        // Category Title
        val category: TextView = v.findViewById(R.id.category)

        fun setViewPager(data: NewsListVO, anim: Boolean) {  // View Page
            pagerAdapter = PagerAdapter(context)
            pagerAdapter.setNewsData(data, anim)

            viewPager.adapter = pagerAdapter

            indicator.setViewPager(viewPager)

            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    currentPage = position
                    if (viewPager.description != null) {
                        viewPager.description.clearAnimation()
                        viewPager.description.visibility = View.INVISIBLE
                        pagerAdapter.stopAnim()
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }

        fun setAutoSwipe() { // Auto Swipe using Timer()
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

    fun startAnimation(position: Int) {
        if (position != -1 && currentCategory != position) {
            notifyItemInserted(position)  // Adapter에 추가

            notifyItemRemoved(position) // Adapter에 삭제

            notifyItemChanged(position) // Adapter View의 데이터만 변경

            notifyItemRangeChanged(0, itemCount) // Adapter의 0~10번VIew들의 데이터만 변경

            currentCategory = position
        }
    }

    fun setData(data: Array<NewsListVO>?){
        mData = data
        notifyDataSetChanged()
    }
}