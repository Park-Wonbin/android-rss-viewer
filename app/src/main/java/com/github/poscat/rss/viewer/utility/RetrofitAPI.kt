package com.github.poscat.rss.viewer.utility

import com.github.poscat.rss.viewer.model.Channel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("/api/v1/channel")
    fun getChannels(): Observable<List<Channel>>

    @GET("/api/v1/channel/items/count/10")
    fun getChannelsWithItems(): Observable<List<Channel>>

    @GET("/api/v1/channel/items/count/10")
    fun getSelectedChannelsWithItems(@Query("id") vararg channelId: String): Observable<List<Channel>>
}