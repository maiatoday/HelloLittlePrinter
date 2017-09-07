package net.maiatoday.hellolittleprinter

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.content_run_test.*
import net.maiatoday.hellolittleprinter.util.toast
import net.maiatoday.printer.BluetoothCallback
import net.maiatoday.printer.BluetoothWrapper
import java.text.SimpleDateFormat
import java.util.*

class RunTestActivity : LifecycleActivity(), BluetoothCallback {

    companion object {
        const val TAG = "RunTestActivity"
        const val EXTRA_INTERVAL = "interval"
        const val EXTRA_DEVICE_ADDRESS = "address"
        const val KEY_IS_RUNNING = "isRunning"
    }

    val timerHandler = Handler()
    val timerRunnable: Runnable = Runnable {
        if (isRunning) {
            incrementCount()
            sendPrintJob()
        }
    }

    private var count = 0
    private lateinit var startTime: Date
    private var interval: Long = 0
    private var deviceAddress: String = ""
    private var isRunning: Boolean = true

    lateinit var preferences: Prefs

    lateinit var bluetoothWrapper: BluetoothWrapper

    var lastInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_test)
        // setSupportActionBar(toolbar)
        preferences = Prefs(this)

        bluetoothWrapper = BluetoothWrapper(this, lifecycle, this)
        val intervalString = intent.getStringExtra(EXTRA_INTERVAL)
        deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS)
        interval = intervalString.toLong()
        textInterval.text = "$intervalString seconds"
        buttonStop.setOnClickListener { view ->
            setRunningState(!isRunning)
        }
        lastInstanceState = savedInstanceState
        bluetoothWrapper.connectToPrinter(deviceAddress)

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
            incrementCount()
            sendPrintJob()
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
        bluetoothWrapper.disconnectDevice()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MainActivity.REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    bluetoothWrapper.enable()
                    bluetoothWrapper.connectToPrinter(deviceAddress)

                } else {
                    // User did not enable Bluetooth or an error occured
                    toast("Bluetooth not enabled!")
                }

            }

        }
    }

    private fun setRunningState(running: Boolean) {
        isRunning = running
        if (isRunning) {
            textRunning.visibility = View.VISIBLE
            textRunning.text = "Running"
            textStopped.visibility = View.GONE
            buttonStop.text = "Stop"
            startTest()
        } else {
            textRunning.visibility = View.GONE
            textStopped.visibility = View.VISIBLE
            textStopped.text = "Stopped"
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
        incrementCount()
        sendPrintJob()
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
        textRunTime.text = preferences.testLength()
    }


    private fun sendPrintJob() {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val now = df.format(Date().time)
        val message = "$now \nsend job #$count to printer"
        toast(message)
        val body = listOf("Dragon's Revelry Starcake ", "2 x White Cake", "50 x Saffron Thread", "40 x Piece of Zhaitaffy", "1 x Crystalline Dust")
        SampleReceipt.print(this,
                false,
                R.drawable.hello_little_priniter_logo,
                count,
                "OHAI there",
                body,
                "Total       2048 Gold",
                "---Society of little Printers---",
                "De Nada!",
                now,
                bluetoothWrapper
        )
    }


    override fun requestBTEnable() {
        val enableIntent = Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, MainActivity.REQUEST_ENABLE_BT)
    }

    override fun popToast(message: String?) {
        //toast(message)
        Log.d(TAG, "popToast " + message)
    }

    override fun connecting(name: String?) {
        textRunning.visibility = View.VISIBLE
        textStopped.visibility = View.GONE
        textRunning.text = "Connecting"
    }

    override fun deviceConnected(name: String?, address: String?) {

        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val now = df.format(Date().time)
        val body = listOf("Device connected", "Starting test")
        SampleReceipt.print(this,
                true,
                R.drawable.hello_little_priniter_logo,
                count,
                "Ping!",
                body,
                "Pong!",
                "---Society of little Printers---",
                "Boom!",
                now,
                bluetoothWrapper
        )
        if (lastInstanceState == null) {
            setRunningState(true)
        } else {
            isRunning = lastInstanceState!!.getBoolean(KEY_IS_RUNNING)
            restoreRunningState()
        }
    }

    override fun deviceDisconnected() {
        setRunningState(false)
    }

}
