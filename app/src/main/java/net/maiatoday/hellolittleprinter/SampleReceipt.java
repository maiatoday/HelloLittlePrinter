package net.maiatoday.hellolittleprinter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.util.Log;

import net.maiatoday.printer.BluetoothWrapper;
import net.maiatoday.printer.sdk.Command;
import net.maiatoday.printer.sdk.PrintPicture;
import net.maiatoday.printer.sdk.PrinterCommand;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by maia on 2017/09/07.
 */

public class SampleReceipt {
    private static final String TAG = "SampleReceipt";

    public static void print(Context context,
                             boolean barcode,
                             @DrawableRes int logoDrawableId,
                             int referenceNumber,
                             String title,
                             List<String> body,
                             String biggerText,
                             String inverse,
                             String footer,
                             String dateString,
                             BluetoothWrapper bluetoothWrapper) {
        try {

            bluetoothWrapper.send(Command.ESC_Init);
            bluetoothWrapper.send(Command.LF);

            Bitmap logo = BitmapFactory.decodeResource(context.getResources(),
                    logoDrawableId);
            byte[] data;
            if (logo != null && barcode == false) {
                int nMode = 0;
                int nPaperWidth = 384;
                data = PrintPicture.POS_PrintBMP(logo, nPaperWidth, nMode);
            } else {
                data = PrinterCommand.getBarCommand("https://github.com/maiatoday/HelloLittlePrinter", 0, 3, 4);
            }
            Command.ESC_Align[2] = Command.ALIGN_CENTER;
            bluetoothWrapper.send(Command.ESC_Align);
            bluetoothWrapper.send(data);

            bluetoothWrapper.send(Command.LF);
            bluetoothWrapper.send(Command.LF);
            bluetoothWrapper.send(Command.LF);

            Command.ESC_Align[2] = Command.ALIGN_CENTER;
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            bluetoothWrapper.send((title + "\n").getBytes("GBK"));

            Command.ESC_Align[2] = Command.ALIGN_CENTER;
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            bluetoothWrapper.send("\n================================\n".getBytes("GBK"));

            Command.ESC_Align[2] = Command.ALIGN_LEFT; //left align
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            for (String s : body) {
                bluetoothWrapper.send(s + "\n");
            }

            Command.ESC_Align[2] = Command.ALIGN_RIGHT;
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            bluetoothWrapper.send(Command.BOLD);
            bluetoothWrapper.send((biggerText + "\n").getBytes("GBK"));
            bluetoothWrapper.send(Command.BOLD_CANCEL);

            Command.ESC_Align[2] = Command.ALIGN_CENTER;
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            bluetoothWrapper.send("\n================================\n".getBytes("GBK"));

            Command.ESC_Align[2] = Command.ALIGN_CENTER;
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            bluetoothWrapper.send((footer + "\n").getBytes("GBK"));

            Command.ESC_Align[2] = Command.ALIGN_CENTER;
            bluetoothWrapper.send(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            bluetoothWrapper.send(Command.GS_ExclamationMark);
            bluetoothWrapper.send(Command.REVERSE);
            bluetoothWrapper.send((inverse + "\n").getBytes("GBK"));
            bluetoothWrapper.send(Command.REVERSE_CANCEL);

            Command.ESC_Align[2] = Command.ALIGN_RIGHT;
            bluetoothWrapper.send(Command.ESC_Align);
            bluetoothWrapper.send("Reference "+referenceNumber+"\n");
            Command.ESC_Align[2] = Command.ALIGN_RIGHT;  //right align
            bluetoothWrapper.send(Command.ESC_Align);
            bluetoothWrapper.send(dateString);
            bluetoothWrapper.send(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
            bluetoothWrapper.send(Command.GS_V_m_n);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "print: problem with sample receipt encoding");
        }
    }
}
