package com.nwhacks.superteam.nfckiosk;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created by theal on 2/27/2016.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[100];  // buffer store for the stream
        int bytes; // bytes returned from read()

        Bitmap bmp =  BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        BufferedInputStream buf = new BufferedInputStream(mmInStream, 8096);

        // Keep listening to the InputStream until an exception occurs
        while (!(bmp != null)){
            for (int i = 0; i < 1; i++) {
                try {
                    String request = "sendmeimage";
                    byte[] requestBuffer = request.getBytes("UTF-8");
                    //write(requestBuffer);

                    buffer = new byte[buf.available()];
                    buf.read(buffer);

                    bmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                    String s = new String(buffer, 0, 40);
                    Log.d("BLUETOOTH INPUT", "" + s);
                } catch (Exception e) {
                    break;
                }
            }
        }
        try {mmInStream.close();}
        catch (Exception e) {}
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            for(int i = 0; i < 1000; i++)
                mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}