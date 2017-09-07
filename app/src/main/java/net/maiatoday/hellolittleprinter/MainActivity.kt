package net.maiatoday.hellolittleprinter

import android.app.Activity
import android.arch.lifecycle.LifecycleActivity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.maiatoday.hellolittleprinter.util.toast
import net.maiatoday.printer.BluetoothCallback
import net.maiatoday.printer.DeviceListActivity

class MainActivity : LifecycleActivity(), BluetoothCallback {

    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_CONNECT_DEVICE = 100
        const val REQUEST_ENABLE_BT = 101
    }

    private var isConnected: Boolean = false

    lateinit var preferences: Prefs

   // lateinit var bluetoothWrapper: BluetoothWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO fix setSupportActionBar(toolbar)
        preferences = Prefs(this)
    //    bluetoothWrapper = BluetoothWrapper(this, lifecycle, this)

        fab.setOnClickListener { view ->
            showPreferences()
        }
        butttonStart.setOnClickListener { view ->
            val interval = editInterval.text.toString()
            preferences.interval = interval.toInt()
            val i = Intent(this, RunTestActivity::class.java)
            i.putExtra(RunTestActivity.EXTRA_INTERVAL, interval)
            i.putExtra(RunTestActivity.EXTRA_DEVICE_ADDRESS, preferences.lastDeviceAddress)
            startActivity(i)
        }
        buttonScan.setOnClickListener { view ->
            showListOfPairedDevices()

        }
        buttonDisconnect.setOnClickListener { view ->
            disconnectFromPrinter()
        }
        editInterval.setText(preferences.interval.toString())
        if (!TextUtils.isEmpty(preferences.lastDeviceAddress) &&
                !TextUtils.isEmpty(preferences.lastDeviceName)) {
            isConnected = true //TODO this is work around because we don't really connect here
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CONNECT_DEVICE -> {if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                val address = data?.getExtras()?.getString(
                        DeviceListActivity.EXTRA_DEVICE_ADDRESS)
                val name  = data?.getExtras()?.getString(
                        DeviceListActivity.EXTRA_DEVICE_NAME)
                if (address != null && name != null) {
                    connectToPrinter(address, name)
                }
            }
            }
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
     //               bluetoothWrapper.enable()
                } else {
                    // User did not enable Bluetooth or an error occured
                    toast("Bluetooth not enabled!")
                }

            }

        }
    }

    private fun setConnectionStateOnUI() {
        if (isConnected) {
            val printerName = preferences.lastDeviceName
            val printerAddress = preferences.lastDeviceAddress
            textStatus.text = "Selected $printerName \n $printerAddress"
            buttonDisconnect.visibility = View.VISIBLE
            buttonScan.visibility = View.GONE
            butttonStart.isEnabled = true
        } else {
            textStatus.text = "No printer selected"
            buttonDisconnect.visibility = View.GONE
            buttonScan.visibility = View.VISIBLE
            butttonStart.isEnabled = false
        }
    }


    private fun connectToPrinter(address: String, name: String) {
   //     bluetoothWrapper.connectToPrinter(address)
        //TODO remove test
        deviceConnected(name, address)

    }


    private fun disconnectFromPrinter() {
   //     bluetoothWrapper.disconnectDevice()
        //TODO remove test
        deviceDisconnected()

    }

    private fun showListOfPairedDevices() {
        val intent = Intent(this, DeviceListActivity::class.java)
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE)
    }


    private fun showPreferences() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Results")
                .setMessage(preferences.toString())
        val dialog = builder.create()
        dialog.show()

    }


    override fun requestBTEnable() {
        val enableIntent = Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
    }

    override fun popToast(message: String?) {
       // toast(CharSequence(message))
        Log.d(TAG, message)
    }

    override fun deviceConnected(name: String, address: String) {
        isConnected = true
        preferences.lastDeviceAddress = address
        preferences.lastDeviceName = name
        setConnectionStateOnUI()
    //    bluetoothWrapper.send("Hello World! you are connected to $printerName with address $printerAddress")
    }

    override fun deviceDisconnected() {

        isConnected = false
        preferences.lastDeviceAddress = ""
        preferences.lastDeviceName = ""
        setConnectionStateOnUI()
    }


    override fun connecting(name: String?) {

    }
}


