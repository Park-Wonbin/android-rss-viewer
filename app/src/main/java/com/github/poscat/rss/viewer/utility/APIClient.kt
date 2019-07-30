package com.github.poscat.rss.viewer.utility

import com.github.poscat.rss.viewer.model.Channel
import com.github.poscat.rss.viewer.model.Zipper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {
    private val mRetrofitAPI = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitAPI::class.java)

    private fun createZipper(
        observable1: Observable<List<Channel>>,
        observable2: Observable<List<Channel>>
    ): Observable<Zipper> {
        return Observable.zip(observable1, observable2,
            BiFunction<List<Channel>, List<Channel>, Zipper> { channels, items ->
                Zipper(channels, items)
            })
    }

    fun getChannelsAPI(): Observable<Zipper> {
        val observe1 = mRetrofitAPI.getChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val observe2 = mRetrofitAPI.getChannelsWithItems()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        return createZipper(observe1, observe2)
    }

    fun getSelectedChannelsAPI(ids: Array<String>): Observable<Zipper> {
        val observe1 = mRetrofitAPI.getChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val observe2 = mRetrofitAPI.getSelectedChannelsWithItems(*ids)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        return createZipper(observe1, observe2)
    }

    fun getSelectedChannelCountAPI(id: String, count: Int): Observable<Zipper> {
        val observe1 = mRetrofitAPI.getChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val observe2 = mRetrofitAPI.getSelectedChannelCountItems(count, id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        return createZipper(observe1, observe2)
    }
}