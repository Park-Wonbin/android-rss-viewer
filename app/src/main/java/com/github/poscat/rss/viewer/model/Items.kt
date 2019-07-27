package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("link") var link: String,
    @SerializedName("published") var published: String,
    @SerializedName("author") var author: Author,
    @SerializedName("guid") var guid: String,
    @SerializedName("enclosures") var enclosures: ArrayList<Contents>? = null
)