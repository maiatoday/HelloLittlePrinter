package net.maiatoday.printer;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import static android.arch.lifecycle.Lifecycle.State.STARTED;

/**
 * Bluetooth wrapper that is a lifecycle observer so it can handle it's own create and cleanup
 * Created by maia on 2017/09/05.
 */

public class BluetoothWrapper implements LifecycleObserver, BluetoothMessageHandler.BluetoothMessages {

    private static final String TAG = "BluetoothListener";

    private final Lifecycle lifecycle;
    private final BluetoothAdapter adapter;
    private final BluetoothCallback callback;
    private boolean enabled = false;
    BluetoothService service;
    private Handler handler = new BluetoothMessageHandler(this);
    private String deviceName = "";
    private String deviceAddress = "";

    public BluetoothWrapper(Context context, Lifecycle lifecycle, BluetoothCallback callback) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
        this.callback = callback;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            service = new BluetoothService(context, handler);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void resume() {
        if (adapter != null && service != null) {

            if (!adapter.isEnabled()) {
                callback.requestBTEnable();
            }
        }
    }

    public void enable() {
        enabled = true;
        if (lifecycle.getCurrentState().isAtLeast(STARTED)) {
            // connect if not connected
            if (adapter != null && service != null && adapter.isEnabled()) {
                service.start(); //TODO is this right?
                //TODO reconnect to printer if we were connected?
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        if (service != null) {
            service.stop();
        }
    }

    //    public void connectToPrinter(@org.jetbrains.annotations.NotNull String address) {
    public void connectToPrinter(String address) {
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = adapter
                    .getRemoteDevice(address);
            // Attempt to connect to the device
            service.connect(device);
            deviceName = device.getName();
            deviceAddress = address;

        }
    }

    public void disconnectDevice() {
        callback.deviceDisconnected();
    }

    public void send(byte[] data) {
        if (service.getState() == BluetoothService.STATE_CONNECTED) {
            service.write(data);
        }
    }

    public void send(String data) {
        if (data.length() > 0) {
            try {
                service.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "send: could not send string with unsupported character set");
            }
        }
    }

    @Override
    public void stateConnected() {
        callback.deviceConnected(deviceName, deviceAddress);
    }

    @Override
    public void stateConnecting() {
       callback.connecting(deviceName);
    }

    @Override
    public void stateListenNone() {
        callback.deviceDisconnected();
    }

    @Override
    public void deviceName(String deviceName) {

    }

    @Override
    public void toast(String message) {
        callback.popToast(message);
    }

    @Override
    public void connectionLost() {
        callback.popToast(deviceName + " disconnected");
        callback.deviceDisconnected();
    }

    @Override
    public void unableToConnect() {
        callback.popToast("Unable to connect");
    }

    public static boolean checkBluetoothOk() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null ) {
            if (defaultAdapter.isEnabled()) {
                return true;
            }
        }
        return false;
    }
}
