package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String = "",
    @SerializedName("link") var link: String = "",
    @SerializedName("published") var published: String = "",
    @SerializedName("author") var author: Author? = null,
    @SerializedName("guid") var guid: String,
    @SerializedName("enclosures") var enclosures: ArrayList<Enclosure>? = null
)