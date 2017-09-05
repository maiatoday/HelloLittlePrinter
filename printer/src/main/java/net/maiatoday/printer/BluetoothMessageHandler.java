package net.maiatoday.printer;

import android.os.Handler;
import android.os.Message;

import static net.maiatoday.printer.BluetoothService.DEVICE_NAME;
import static net.maiatoday.printer.BluetoothService.MESSAGE_CONNECTION_LOST;
import static net.maiatoday.printer.BluetoothService.MESSAGE_DEVICE_NAME;
import static net.maiatoday.printer.BluetoothService.MESSAGE_READ;
import static net.maiatoday.printer.BluetoothService.MESSAGE_STATE_CHANGE;
import static net.maiatoday.printer.BluetoothService.MESSAGE_TOAST;
import static net.maiatoday.printer.BluetoothService.MESSAGE_UNABLE_CONNECT;
import static net.maiatoday.printer.BluetoothService.MESSAGE_WRITE;
import static net.maiatoday.printer.BluetoothService.TOAST;

/**
 * Created by maia on 2017/09/05.
 */

public class BluetoothMessageHandler extends Handler {
    private static final String TAG = "BluetoothMessageHandler";
    private BluetoothMessages callback;

    public BluetoothMessageHandler(BluetoothMessages callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
//                if (DEBUG)
//                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        callback.stateConnected();
                        break;
                    case BluetoothService.STATE_CONNECTING:
                        callback.stateConnecting();
                        break;
                    case BluetoothService.STATE_LISTEN:
                    case BluetoothService.STATE_NONE:
                        callback.stateListenNone();
                        break;
                }
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_READ:
                break;
            case MESSAGE_DEVICE_NAME:
                String deviceName = msg.getData().getString(DEVICE_NAME);
                callback.deviceName(deviceName);
                break;
            case MESSAGE_TOAST:
                String message = msg.getData().getString(TOAST);
                callback.toast(message);
                break;
            case MESSAGE_CONNECTION_LOST:
                callback.connectionLost();
                break;
            case MESSAGE_UNABLE_CONNECT:
                callback.unableToConnect();
                break;
        }
    }


public interface BluetoothMessages {

    void stateConnected();

    void stateConnecting();

    void stateListenNone();

    void deviceName(String deviceName);

    void toast(String message);

    void connectionLost();

    void unableToConnect();
}

}