package com.github.poscat.rss.viewer.DataType

import android.graphics.Bitmap

class News {
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
    var img: Bitmap? = null
}