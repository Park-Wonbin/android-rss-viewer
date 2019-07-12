package com.binvitstudio.android_live_slider

class NewsListVO {
    lateinit var id: String
    lateinit var rss_link: String
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
        lateinit var author: Author
        lateinit var guid: String
        var enclosures: ArrayList<Contents>? = null

        inner class Contents {
            lateinit var url: String
            lateinit var type: String
        }

        inner class Author {
            lateinit var name: String
        }
    }
}