package com.github.poscat.rss.viewer.utility

import java.text.SimpleDateFormat
import java.util.*

object TimeFormat {
    private const val SEC: Int = 60
    private const val MIN: Int = 60
    private const val HOUR: Int = 24
    private const val DAY: Int = 30
    private const val MONTH: Int = 12

    fun formatTimeString(str: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        val regTime = df.parse(str).time

        val curTime: Long = System.currentTimeMillis()
        var diffTime: Long = (curTime - regTime) / 1000

        if (diffTime < SEC) return "방금 전"
        diffTime /= SEC

        if (diffTime < MIN) return "" + diffTime + "분 전"
        diffTime /= MIN

        if (diffTime < HOUR) return "" + diffTime + "시간 전"
        diffTime /= HOUR

        if (diffTime < DAY) return "" + diffTime + "일 전"
        diffTime /= DAY

        if (diffTime < MONTH) return "" + diffTime + "달 전"
        diffTime /= MONTH

        return "" + diffTime + "년 전"
    }
}