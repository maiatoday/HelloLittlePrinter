package net.maiatoday.printer;

import android.os.Handler;
import android.os.Message;

import static net.maiatoday.printer.BluetoothService.MESSAGE_STATE_CHANGE;

/**
 * Created by maia on 2017/09/05.
 */

public class BluetoothMessageHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                break;
            default:
                break;
        }
    }

}