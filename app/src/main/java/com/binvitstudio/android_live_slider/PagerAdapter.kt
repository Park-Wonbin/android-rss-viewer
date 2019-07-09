package com.binvitstudio.android_live_slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.page.view.*

class PagerAdapter(private val mContext: Context) : PagerAdapter() {
    var view: View? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        if (mContext != null) {
            val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.page, container, false)

            view!!.title.text = "Title" + position
            view!!.description.text = "Description" + position
            view!!.description.clearAnimation()
            view!!.description.visibility = View.INVISIBLE
            view!!.image.animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom)
        }

        container.addView(view)

        return view!!
    }

    fun stopAnim() {
        view!!.description.clearAnimation()
        view!!.description.visibility = View.INVISIBLE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun getCount(): Int {
        return 5
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        view.description.clearAnimation()
        view.description.visibility=View.INVISIBLE
        view.description.animation = AnimationUtils.loadAnimation(mContext, R.anim.show)

        return view === `object`
    }
}