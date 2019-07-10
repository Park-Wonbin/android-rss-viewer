package com.binvitstudio.android_live_slider

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitAPI {
    @GET("/api/v1/channel/items")
    fun getNewsList(): Call<String>
}