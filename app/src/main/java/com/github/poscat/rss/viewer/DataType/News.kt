package com.github.poscat.rss.viewer.DataType

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("id") var id: String,
    @SerializedName("rsslink") var rsslink: String,
    @SerializedName("title") var title: String,
    @SerializedName("link") var link: String,
    @SerializedName("items") var items: ArrayList<Items>? = null,
    @SerializedName("feedType") var feedType: String,
    @SerializedName("feedVersion") var feedVersion: String
)

data class Items(
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("link") var link: String,
    @SerializedName("published") var published: String,
    @SerializedName("author") var author: Author,
    @SerializedName("guid") var guid: String,
    @SerializedName("enclosures") var enclosures: ArrayList<Contents>? = null
)

data class Contents (
    @SerializedName("url") var url: String,
    @SerializedName("type") var type: String
)

data class Author (
    @SerializedName("name") var name: String
)