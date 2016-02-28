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
import java.util.ArrayList;
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
        ArrayList<byte[]> buffers2 = new ArrayList<byte[]>();

        Bitmap bmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        BufferedInputStream buf = new BufferedInputStream(mmInStream, 8096);
        // Keep listening to the InputStream until an exception occurs
        while(true) {
            try {
                if(buf.available() == 0){
                    break;
                }
                buffer = new byte[buf.available()];
                buf.read(buffer);
                buffers2.add(buffer);

            } catch (Exception e) {
                break;
            }
        }
        try {mmInStream.close();}
        catch (Exception e) {}

        byte[] hyperbuffer = {};

        for(byte[] bufferpiece : buffers2){
            byte[] nextHyperbuffer = new byte[hyperbuffer.length + bufferpiece.length];
            System.arraycopy(
                    hyperbuffer, 0,
                    nextHyperbuffer, 0,
                    hyperbuffer.length);
            System.arraycopy(
                    bufferpiece, 0,
                    nextHyperbuffer, hyperbuffer.length,
                    bufferpiece.length);
            hyperbuffer = nextHyperbuffer;
        }

        bmp = BitmapFactory.decodeByteArray(hyperbuffer, 0, hyperbuffer.length);
        bmp.toString();

    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            for(int i = 0; i < 1; i++)
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