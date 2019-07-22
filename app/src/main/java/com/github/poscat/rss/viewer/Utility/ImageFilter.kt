package com.github.poscat.rss.viewer.Utility

import android.graphics.Bitmap
import com.squareup.picasso.Transformation
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter

class ImageFilter: Transformation {

    override fun key(): String {
        return "filter"
    }

    override fun transform(source: Bitmap?): Bitmap {
        System.loadLibrary("NativeImageProcessor")

        var myFilter = Filter()
        /**
         * If you want your photo filter added to it. I just lowered the brightness.
         * Please refer to the library below.
         *
         * https://github.com/Zomato/AndroidPhotoFilters
         */
        myFilter.addSubFilter(BrightnessSubFilter(-25))

        if (source!= null) {
            var inputImage: Bitmap = source!!.copy(source.config, true)
            source.recycle()
            return myFilter.processFilter(inputImage)
        }
        return source!!
    }
}