package net.maiatoday.hellolittleprinter

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_run_test.*

class MainActivity : AppCompatActivity() {

    private var isConnected: Boolean = false
    private var printerName: String = ""

    lateinit var preferences: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        preferences = Prefs(this)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Results of last test", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            //TODO show results of last test
        }
        butttonStart.setOnClickListener { view ->
            val interval = editInterval.text.toString()
            preferences.interval = interval.toInt()
            val i = Intent(this, RunTestActivity::class.java)
            i.putExtra(RunTestActivity.EXTRA_INTERVAL, interval)
            startActivity(i)
        }
        buttonScan.setOnClickListener { view ->
            showListOfPairedDevices()

        }
        buttonDisconnect.setOnClickListener { view ->
            disconnectFromPrinter()
        }
        editInterval.setText(preferences.interval.toString())
        setConnectionStateOnUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setConnectionStateOnUI() {
        if (isConnected) {
            textStatus.text = "Connected to printer $printerName"
            buttonDisconnect.visibility = View.VISIBLE
            buttonScan.visibility = View.GONE
            butttonStart.isEnabled = true
        } else {
            textStatus.text = "Not connected to printer"
            buttonDisconnect.visibility = View.GONE
            buttonScan.visibility = View.VISIBLE
            butttonStart.isEnabled = false
        }
    }


    private fun connectToPrinter(name: String) {
        //TODO connect to printer
        printerName = name
        isConnected = true
        setConnectionStateOnUI()
    }


    private fun disconnectFromPrinter() {
        //TODO disconnect from the printer
        printerName = ""
        isConnected = false
        setConnectionStateOnUI()
    }

    private fun showListOfPairedDevices() {
        //TODO start show list of paired devices
        connectToPrinter("blahblah")
    }
}
