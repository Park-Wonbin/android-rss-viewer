package com.poapper.liveslider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class LiveSliderPagerAdapter<T> : PagerAdapter() {
    protected lateinit var context: Context
    protected var feed : Feed<T>? = null
    protected var animation : Boolean = false

    fun setData(feed: Feed<T>?, animation: Boolean) {
        this.feed = feed
        this.animation = animation
    }

    fun setAdapterContext(context: Context) {
        this.context = context
    }

    abstract fun stopAnimation()

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return feed?.items?.size ?: 0
    }
}