package com.binvitstudio.android_live_slider

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitAPI {
    @GET("/api/v1/channel/items")
    fun getNewsList(): Call<String>

    @GET("/api/v1/channel/items/searchWord/{word}")
    fun getSearchList(@Path("word") word: String): Call<String>
}