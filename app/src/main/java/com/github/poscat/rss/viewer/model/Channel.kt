package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class Channel (
    @SerializedName("id") var id: String,
    @SerializedName("rsslink") var rsslink: String,
    @SerializedName("title") var title: String
)