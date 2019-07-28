package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class Enclosure (
    @SerializedName("url") var url: String,
    @SerializedName("type") var type: String
)