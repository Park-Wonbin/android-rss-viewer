package com.github.poscat.rss.viewer.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.MediaController
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.poscat.liveslider.LiveSliderPagerAdapter
import com.github.poscat.rss.viewer.activity.NewsActivity
import com.github.poscat.rss.viewer.model.Items
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.utility.ImageFilter
import com.github.poscat.rss.viewer.utility.TimeFormat
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

        if (item.enclosures != null) {
            if (item.enclosures!![0].type.contains("image")) {
                view.video.visibility = View.INVISIBLE
                // Set Image
                if (item.enclosures != null)
                    Glide.with(context).load(item.enclosures!![0].url)
                        .placeholder(R.drawable.loading_img)
                        .transform(ImageFilter())
                        .error(R.drawable.default_img)
                        .into(view.image)
            } else if (item.enclosures!![0].type.contains("video")) {
                view.video.visibility = View.VISIBLE
                // Set Video
                val mediaController = MediaController(context)
                mediaController.setAnchorView(view.video)
                // Set video link
                val video = Uri.parse(item.enclosures!![0].url)
                // view.video.setMediaController(mediaController);
                view.video.setMediaController(null)
                view.video.setVideoURI(video)
                view.video.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                    override fun onPrepared(mp: MediaPlayer) {
                        mp.setVolume(0f, 0f)
                    }
                })
                view.video.requestFocus()
            }
        } else {
            view.video.visibility = View.INVISIBLE
        }

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
        if (view.video != null) view.video.start()
    }

    override fun stopAnimation(context: Context, view: View) {
        view.description.clearAnimation()
        view.description.visibility = View.INVISIBLE
        view.image.clearAnimation()
        if (view.video != null) view.video.pause()
    }
}