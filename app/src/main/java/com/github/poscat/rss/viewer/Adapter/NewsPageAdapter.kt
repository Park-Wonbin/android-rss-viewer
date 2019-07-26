package com.github.poscat.rss.viewer.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.poscat.liveslider.LiveSliderPagerAdapter
import com.github.poscat.rss.viewer.Activity.NewsActivity
import com.github.poscat.rss.viewer.DataType.Items
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.Utility.ImageFilter
import com.github.poscat.rss.viewer.Utility.TimeFormat
import kotlinx.android.synthetic.main.page.view.*

class NewsPageAdapter : LiveSliderPagerAdapter<Items>() {
    override fun createView(context: Context, container: ViewGroup, item: Items): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.page, container, false)

        // Set Author & Time
        if (item.author.name == null) view.creator_time.text = TimeFormat.formatTimeString(item.published)
        else view.creator_time.text = item.author.name + "  ·  " + TimeFormat.formatTimeString(item.published)

        // Set Title
        view.title.text = item.title

        // Set Description
        val sb = SpannableStringBuilder()
        val attrAdditional = SpannableString(item.description)
        attrAdditional.setSpan(
            BackgroundColorSpan(Color.parseColor("#B3000000")),
            0, item.description.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        sb.append(attrAdditional)

        view.description.setText(sb, TextView.BufferType.NORMAL)

        // Set Image
        if (item.enclosures != null)
            Glide.with(context).load(item.enclosures!![0].url)
                .placeholder(R.drawable.loading_img)
                .transform(ImageFilter())
                .error(R.drawable.default_img)
                .into(view.image)

        // Set Link
        view.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra("news_url", item.link)
            view.context.startActivity(intent)
        }

        return view
    }

    override fun startAnimation(context: Context, view: View) {
        view.image.startAnimation(AnimationUtils.loadAnimation(context, R.anim.zoom))
        view.description.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    override fun stopAnimation(context: Context, view: View) {
        view.description.clearAnimation()
        view.description.visibility = View.INVISIBLE
        view.image.clearAnimation()
    }
}