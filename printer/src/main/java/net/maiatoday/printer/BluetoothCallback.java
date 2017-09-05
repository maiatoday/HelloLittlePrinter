package net.maiatoday.printer;

/**
 * Created by maia on 2017/09/05.
 */

public interface BluetoothCallback {
    void requestBTEnable();
    void deviceConnected(String name, String address);
    void deviceDisconnected();
}
