package com.github.poscat.rss.viewer.utility

import com.github.poscat.rss.viewer.model.Channel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("/api/v1/channel")
    fun getChannels(): Observable<List<Channel>>

    @GET("/api/v1/channel/items/count/{count}")
    fun getChannelsWithItems(@Path("count") count: Int): Observable<List<Channel>>

    @GET("/api/v1/channel/items/count/{count}")
    fun getSelectedChannelsWithItems(@Path("count") count: Int, @Query("id") vararg channelId: String): Observable<List<Channel>>
}