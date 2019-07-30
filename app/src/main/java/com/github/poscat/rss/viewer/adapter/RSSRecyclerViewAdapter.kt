package com.github.poscat.rss.viewer.adapter

import android.content.Context
import android.content.Intent
import com.github.poscat.liveslider.LiveSliderAdapter
import com.github.poscat.liveslider.LiveSliderFeed
import com.github.poscat.liveslider.LiveSliderPagerAdapter
import com.github.poscat.rss.viewer.activity.RecentNewsActivity
import com.github.poscat.rss.viewer.model.Item

class RSSRecyclerViewAdapter(context: Context, pagerAdapter: LiveSliderPagerAdapter<Item, Int>) :
    LiveSliderAdapter<Item, Int>(context, pagerAdapter, true) {
    override fun setHolderListener(holder: LiveSliderViewHolder, feed: LiveSliderFeed<Item, Int>, context: Context) {
        holder.category.setOnClickListener {
            val intent = Intent(context, RecentNewsActivity::class.java)
            intent.putExtra("title", feed.category)
            intent.putExtra("id", feed.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }
    }
}