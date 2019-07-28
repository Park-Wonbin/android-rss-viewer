package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class Channel (
    @SerializedName("id") var id: String,
    @SerializedName("rsslink") var rsslink: String,
    @SerializedName("title") var title: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("items") var items: ArrayList<Item>? = null,
    @SerializedName("feedType") var feedType: String? = null,
    @SerializedName("feedVersion") var feedVersion: String? = null
)