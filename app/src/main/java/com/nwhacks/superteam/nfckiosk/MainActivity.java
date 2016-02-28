package com.nwhacks.superteam.nfckiosk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Parcelable;
import android.widget.Toast;

import android.nfc.NdefRecord;


import android.app.PendingIntent;
import android.widget.TextView;

import android.content.ContextWrapper;
import java.io.File;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.nwhacks.superteam.nfckiosk.imageHandler.imageViewHandler;




public class MainActivity extends AppCompatActivity {

    Intent intent;

    IntentFilter[] readTagFilters;
    PendingIntent pendingIntent;
    Tag detectedTag;

    NdefMessage[] msgs;

    //NFC Adaptor
    private NfcAdapter mNfcAdapter;



    private TextView mTextView;

    private imageViewHandler imgViewHandler = new imageViewHandler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mTextView = (TextView) findViewById(R.id.TextOutput);


        intent = new Intent(this, MainActivity.class);
        msgs = new NdefMessage[]{}; //initialize message array to be empty

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Create a BroadcastReceiver for ACTION_FOUND
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    try {
                        String s = device.getName();
                        Log.d("BLUE TEETH", device.getName() + " " + device.getAddress());
                        if(device.getAddress().equals("30:14:11:14:02:68")){
                            ConnectThread connectThread = new ConnectThread(device);
                            connectThread.start();
                        }
                    }catch (Exception e){
                    }
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();



        /*
        PREPARE YOUR BUTT CUZ THIS NEXT CODE IS MINE (Jeff)
         */

        //Checking for NFC compatability
        if (!mNfcAdapter.isEnabled()) {
            //Shit is good
            Log.d("NFC Reader Doesn't Work"," :( ");

        } else {
            //print("NFC is working on this device.");
            Log.d("NFC Reader is Working"," :):):) ");
        }

        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(this,getClass()).
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter filter2     = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        readTagFilters = new IntentFilter[]{tagDetected,filter2};
        /*
        NO LONGER MY CODE (JEFF)
         */

    }

    protected void onNewIntent(Intent intent) {

        setIntent(intent);

        if(getIntent().getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            readFromTag(getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, readTagFilters, null);


    }


    /*
    JEFF IS BACK AT IT

    This function essentially just processes the NFC intent. Its only called by onNewIntent()
     */
    public void readFromTag(Intent intent){

        Ndef ndef = Ndef.get(detectedTag);


        try{
            ndef.connect();

            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                mTextView.setText(text);


                ndef.close();

            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }
    /*
    No longer my code
     */




    /*

    SAVING IMAGE FILE TO FILE SYSTEM
        -JEFF (AKA: big daddy)

        This shit saves the bitmapImage to /data/data/NFCKioskAndroidApp/app_data/imageDir
     */
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/NFCKioskAndroidApp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Write the image using Lossless compression
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /*
    Sets the image on screen to that of what is saved in the filesystem
    */
    public void setImageView(){

        imgViewHandler.setImage();

    }


}
