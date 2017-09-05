package net.maiatoday.printer;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

import static android.arch.lifecycle.Lifecycle.State.STARTED;

/**
 * Bluetooth listener that is a lifecycle observer so it can handle it's own create and cleanup
 * Created by maia on 2017/09/05.
 */

public class BluetoothListener implements LifecycleObserver {

    private final Lifecycle lifecycle;
    private final BluetoothAdapter adapter;
    private final BluetoothCallback callback;
    private boolean enabled = false;
    BluetoothService service;
    private Handler handler = new BluetoothMessageHandler();

    public BluetoothListener(Context context, Lifecycle lifecycle, BluetoothCallback callback) {
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
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        // disconnect if connected
    }

//    public void connectToPrinter(@org.jetbrains.annotations.NotNull String address) {
    public void connectToPrinter(String address) {
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = adapter
                    .getRemoteDevice(address);
            // Attempt to connect to the device
            service.connect(device);
            callback.deviceConnected(device.getName(), address);
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
}
