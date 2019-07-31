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

class APIClient(private var count: Int) {
    private var mRetrofitAPI: RetrofitAPI
    private var mChannelsObservable: Observable<List<Channel>>
    private var mItemsObservable: Observable<List<Channel>>

    init {
        mRetrofitAPI = Retrofit.Builder().baseUrl("https://rss-search-api.herokuapp.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)

        mChannelsObservable = mRetrofitAPI.getChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        mItemsObservable = mRetrofitAPI.getChannelsWithItems(count)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun createZipper(
        observable1: Observable<List<Channel>>,
        observable2: Observable<List<Channel>>
    ): Observable<Zipper> = Observable.zip(
        observable1, observable2,
            BiFunction<List<Channel>, List<Channel>, Zipper> { channels, items ->
                Zipper(channels, items)
            })

    fun getChannelsAPI(): Observable<Zipper> = createZipper(mChannelsObservable, mItemsObservable)

    fun getSelectedChannelsAPI(ids: Array<String>): Observable<Zipper> {
        val observe = mRetrofitAPI.getSelectedChannelsWithItems(count, *ids)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        return createZipper(mChannelsObservable, observe)
    }

    fun getSelectedItemsAPI(ids: Array<String>): Observable<List<Channel>> =
        mRetrofitAPI.getSelectedChannelsWithItems(count, *ids)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}