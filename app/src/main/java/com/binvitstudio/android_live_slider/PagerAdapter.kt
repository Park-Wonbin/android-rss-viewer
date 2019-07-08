package com.binvitstudio.android_live_slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.page.view.*

class PagerAdapter(private val mContext: Context) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View? = null

        if (mContext != null) {
            val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.page, container, false)

            view.title.text = "Title" + position
        }

        container.addView(view)

        return view!!
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun getCount(): Int {
        return 5
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}