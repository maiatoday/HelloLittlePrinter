package net.maiatoday.hellolittleprinter

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_run_test.*
import kotlinx.android.synthetic.main.content_run_test.*
import net.maiatoday.hellolittleprinter.util.toast
import java.util.*
import java.util.concurrent.TimeUnit

class RunTestActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_INTERVAL = "interval"
        const val KEY_IS_RUNNING = "isRunning"
    }

    val timerHandler = Handler()
    val timerRunnable: Runnable = Runnable {
        if (isRunning) {
            sendPrintJob()
            incrementCount()
        }
    }

    private var count = 0
    private lateinit var startTime : Date
    private var interval: Long = 0
    private var isRunning: Boolean = true

    lateinit var preferences: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_test)
        setSupportActionBar(toolbar)
        preferences = Prefs(this)

        val intervalString = intent.getStringExtra(EXTRA_INTERVAL)
        interval = intervalString.toLong()
        textInterval.text = "$intervalString seconds"
        buttonStop.setOnClickListener { view ->
            setRunningState(!isRunning)
        }
        if (savedInstanceState == null) {
            setRunningState(true)
        } else {
            isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING)
            restoreRunningState()
        }

    }

    private fun restoreRunningState() {
        count = preferences.count
        startTime = Date(preferences.startTimeMs)

        textCount.text = count.toString()
        textStarted.text = startTime.toString()
        updateRunningTime()

        if (isRunning) {
            textRunning.visibility = View.VISIBLE
            textStopped.visibility = View.GONE
            buttonStop.text = "Stop"
            sendPrintJob()
            incrementCount()
        } else {
            textRunning.visibility = View.GONE
            textStopped.visibility = View.VISIBLE
            buttonStop.text = "Start"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(KEY_IS_RUNNING, isRunning)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isRunning) {
            stopTest()
        }
    }

    private fun setRunningState(running: Boolean) {
        isRunning = running
        if (isRunning) {
            textRunning.visibility = View.VISIBLE
            textStopped.visibility = View.GONE
            buttonStop.text = "Stop"
            startTest()
        } else {
            textRunning.visibility = View.GONE
            textStopped.visibility = View.VISIBLE
            buttonStop.text = "Start"
            stopTest()
        }
    }

    private fun stopTest() {
        timerHandler.removeCallbacks(timerRunnable)
        preferences.endTimeMs = Date().time
    }

    private fun startTest() {
        count = 0
        startTestTime()
        sendPrintJob()
        incrementCount()
    }


    private fun startTestTime() {
        startTime = Date()
        preferences.startTimeMs = startTime.time
        textStarted.text = startTime.toString()
    }

    private fun incrementCount() {
        count += 1
        preferences.count = count
        textCount.text = count.toString()
        preferences.endTimeMs = Date().time
        updateRunningTime()
        if (isRunning && interval > 0) {
            timerHandler.postDelayed(timerRunnable, interval * 1000)
        }
    }

    private fun updateRunningTime() {
        val ellapsedMs = preferences.endTimeMs - preferences.startTimeMs
        textRunTime.text =  String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ellapsedMs),
                TimeUnit.MILLISECONDS.toMinutes(ellapsedMs) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(ellapsedMs) % TimeUnit.MINUTES.toSeconds(1));
    }


    private fun sendPrintJob() {
        toast("send job to printer")
    }

}
