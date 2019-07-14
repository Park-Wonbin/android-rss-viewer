package com.binvitstudio.android_live_slider

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.poapper.liveslider.LiveSliderPagerAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.page.view.*

class NewsPageAdapter : LiveSliderPagerAdapter<News>() {
    var view: View? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.page, container, false)
        val description = feed?.items!![position].description + "..."

        view!!.creator.text = feed?.items!![position].author.name
        view!!.time.text = TimeFormat.formatTimeString(feed?.items!![position].published)
        view!!.title.text = feed?.items!![position].title

        view!!.description.text = description
        val span = view!!.description.text as Spannable
        span.setSpan(
            BackgroundColorSpan(Color.parseColor("#B3000000")),
            0,
            view!!.description.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        if (animation) view!!.description.clearAnimation()
        view!!.description.visibility = View.INVISIBLE

        if (feed?.items!![position].enclosures != null)
            Picasso.get().load(feed?.items!![position].enclosures!![0].url).placeholder(R.drawable.test_img).transform(
                ImageFilter()
            ).into(view!!.image)

        if (animation) view!!.image.animation = AnimationUtils.loadAnimation(context, R.anim.zoom)

        view!!.page.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java)
            view!!.context.startActivity(intent)
        }

        container.addView(view)

        return view!!
    }

    override fun stopAnimation() {
        if (animation) {
            view!!.description.clearAnimation()
            view!!.description.visibility = View.INVISIBLE
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        if (animation) {
            view.description.clearAnimation()
            view.description.visibility = View.INVISIBLE
            view.description.animation = AnimationUtils.loadAnimation(context, R.anim.show)
        }

        return view === `object`
    }
}