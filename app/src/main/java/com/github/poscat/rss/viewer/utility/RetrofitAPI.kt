package com.github.poscat.rss.viewer.utility

import com.github.poscat.rss.viewer.model.Channel
import io.reactivex.Observable
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

    @GET("/api/v1/channel")
    fun getChannels(): Observable<List<Channel>>
}