package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("id") var id: String,
    @SerializedName("rsslink") var rsslink: String,
    @SerializedName("title") var title: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("items") var items: ArrayList<Items>? = null,
    @SerializedName("feedType") var feedType: String? = null,
    @SerializedName("feedVersion") var feedVersion: String? = null
)