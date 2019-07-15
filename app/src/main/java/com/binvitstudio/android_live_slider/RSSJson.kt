package com.binvitstudio.android_live_slider

class RSSJson {
    lateinit var id: String
    lateinit var rss_link: String
    lateinit var title: String
    lateinit var link: String
    var items: ArrayList<News>? = null
    lateinit var feedType: String
    lateinit var feedVersion: String
}