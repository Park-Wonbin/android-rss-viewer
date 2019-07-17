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
    override fun createView(context: Context, container: ViewGroup, item: News): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.page, container, false)

        view.creator.text = item.author.name
        view.time.text = TimeFormat.formatTimeString(item.published)
        view.title.text = item.title
        view.description.text = item.description + "..."

        val span = view.description.text as Spannable
        span.setSpan(BackgroundColorSpan(Color.parseColor("#B3000000")),
            0, view.description.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (item.enclosures != null)
            view.image.setImageBitmap(item.img)
            //Picasso.get().load(item.enclosures!![0].url).placeholder(R.drawable.test_img).transform(ImageFilter()).into(view.image)

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