package com.poapper.liveslider

/**
 * A RSS-feed of *items*.
 *
 * @param T the type of the item in this RSS-feed.
 */
class LiveSliderFeed<T> {
    var category = "example"
    var items : ArrayList<T>? = null
}