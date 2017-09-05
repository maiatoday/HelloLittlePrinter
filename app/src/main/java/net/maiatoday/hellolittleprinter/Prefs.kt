package net.maiatoday.hellolittleprinter

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by maia on 2017/09/05.
 */
class Prefs (context: Context) {
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
}