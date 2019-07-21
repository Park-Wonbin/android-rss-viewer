package com.github.poscat.rss.viewer

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("/api/v1/channel/items/count/10")
    fun getNewsList(@Query("id") vararg channelId: String): Call<String>

    @GET("/api/v1/channel/items/count/10")
    fun getNewsListAll(): Call<String>

    @GET("/api/v1/channel")
    fun getChannelList(): Call<String>
}