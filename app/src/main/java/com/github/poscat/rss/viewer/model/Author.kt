package com.github.poscat.rss.viewer.model

import com.google.gson.annotations.SerializedName

data class Author (
    @SerializedName("name") var name: String
)