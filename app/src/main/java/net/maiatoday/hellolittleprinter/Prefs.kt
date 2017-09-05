package net.maiatoday.hellolittleprinter

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by maia on 2017/09/05.
 */
class Prefs(context: Context) {
    companion object {
        const val PREFS_FILENAME = "net.maiatoday.hellolittleprinter.prefs"
        const val INTERVAL = "interval"
        const val COUNT = "count"
        const val START_TIME = "startTime"
        const val END_TIME = "endTime"

    }

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var interval: Int
        get() = prefs.getInt(INTERVAL, 0)
        set(value) = prefs.edit().putInt(INTERVAL, value).apply()

    var count: Int
        get() = prefs.getInt(COUNT, 0)
        set(value) = prefs.edit().putInt(COUNT, value).apply()

    var startTimeMs: Long
        get() = prefs.getLong(START_TIME, 0)
        set(value) = prefs.edit().putLong(START_TIME, value).apply()

    var endTimeMs: Long
        get() = prefs.getLong(END_TIME, 0)
        set(value) = prefs.edit().putLong(END_TIME, value).apply()

    override fun toString(): String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val startime = df.format(startTimeMs)
        val endtime = df.format(endTimeMs)
        val length = testLength()
        return """
    |Start time:      $startime
    |End time:        $endtime
    |No of prints:    $count
    |Interval (s):    $interval
    |Running for:     $length
    """.trimMargin()
    }

    fun testLength(): String {
        val ellapsedMs = endTimeMs - startTimeMs
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ellapsedMs),
                TimeUnit.MILLISECONDS.toMinutes(ellapsedMs) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(ellapsedMs) % TimeUnit.MINUTES.toSeconds(1))
    }
}