package com.github.poscat.rss.viewer.model

import io.reactivex.Observable

class Zipper {
    lateinit var channels: List<Channel>
    lateinit var items: List<Channel>

    constructor(channels: List<Channel>, items: List<Channel>) {
        this.channels = channels
        this.items = items
    }
}