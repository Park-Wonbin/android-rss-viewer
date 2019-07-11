package com.binvitstudio.android_live_slider

import android.graphics.Bitmap
import com.squareup.picasso.Transformation
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter

class ImageFilter: Transformation {

    override fun key(): String {
        return "filter"
    }

    override fun transform(source: Bitmap?): Bitmap {
        System.loadLibrary("NativeImageProcessor")

        var myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(50))
        myFilter.addSubFilter(ContrastSubFilter(1.1f))

        if (source!= null) {
            var inputImage: Bitmap = source!!.copy(source.config, true)
            source.recycle()
            return myFilter.processFilter(inputImage)
        }
        return source!!
    }
}