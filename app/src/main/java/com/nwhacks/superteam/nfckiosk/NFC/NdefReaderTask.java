package com.nwhacks.superteam.nfckiosk.NFC;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import android.nfc.tech.Ndef;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.Arrays;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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


import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.widget.TextView;




/**
 * Created by jeffreydoyle on 2016-02-27.
 *
 *
 * COPIED AND PASTED FROM (Kind of...)
 * http://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
 *
 *
 */


public class NdefReaderTask extends AsyncTask<Tag, Void, String> {


    public static final String TAG = "Output TAG";


    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];


        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        //String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        Charset textEncoding = ((payload[0] & 128) == 0) ? StandardCharsets.UTF_8: StandardCharsets.UTF_16;

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }
/*
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            mTextView.setText("Read content: " + result);
        }
    }
*/




}
