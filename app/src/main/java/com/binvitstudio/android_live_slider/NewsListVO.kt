package com.binvitstudio.android_live_slider

class NewsListVO {
    lateinit var title: String
    lateinit var link: String
    lateinit var items: ArrayList<News>
    lateinit var feedType: String
    lateinit var feedVersion: String

    inner class News {
        lateinit var title: String
        lateinit var description: String
        lateinit var link: String
        lateinit var published: String
        lateinit var guid: String
    }
}