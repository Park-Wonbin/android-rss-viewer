package com.binvitstudio.android_live_slider

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.iterator
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.page.view.*

class PagerAdapter(private val mContext: Context) : PagerAdapter() {

    lateinit var mNewsList: NewsListVO
    var isAnim: Boolean = false
    var viewContainer: ViewGroup? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View? = null

        if (mContext != null) {
            val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.page, container, false)
            view.id = position

            view.creator.text = mNewsList.items!![position].author.name
            view.time.text = TimeFormat.formatTimeString(mNewsList.items!![position].published)
            view.title.text = mNewsList.items!![position].title

            view.description.text = mNewsList.items!![position].description + "..."
            var span = view.description.text as Spannable
            span.setSpan(BackgroundColorSpan(Color.parseColor("#B3000000")), 0, view.description.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (mNewsList.items!![position].enclosures != null)
                Picasso.get().load(mNewsList.items!![position].enclosures!![0].url).placeholder(R.drawable.test_img).transform(ImageFilter()).into(view.image)

            view.setOnClickListener {
                val intent = Intent(mContext, NewsActivity::class.java)
                intent.putExtra("news_url", mNewsList.items!![position].link)
                view.context.startActivity(intent)
            }
        }

        container.addView(view)
        viewContainer = container

        if (position == 0 && isAnim) {
            setAnim(0)
        }

        return view!!
    }

    fun setAnim(position: Int) {
        for (i in viewContainer!!.iterator()) {
            if (i.id == position) {
                Log.v("hihi", "애니메이션 시작됨!")
                i.image.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.zoom))
                i.description.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.show))
            } else {
                i.description.clearAnimation()
                i.description.visibility = View.INVISIBLE
                i.image.clearAnimation()
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
        viewContainer = container
    }
    override fun getCount(): Int {
        if (mNewsList.items == null) return 0
        return mNewsList.items!!.size
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun setNewsData(data: NewsListVO, anim: Boolean) {
        mNewsList = data
        isAnim = anim
    }
}