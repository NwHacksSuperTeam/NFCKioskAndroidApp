package com.nwhacks.superteam.nfckiosk;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class VerifyMifareActivity extends AppCompatActivity {
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private String[][] mTechLists;
    private IntentFilter[] mFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mifare);
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

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };

        Intent intent = getIntent();

        resolveIntent(intent);
    }

    void resolveIntent(Intent intent) {
        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;

            try {       //  5.1) Connect to card
                mfc.connect();
                boolean auth = false;
                String cardData = null;
                // 5.2) and get the number of sectors this card has..and loop thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                for(int j = 2; j < secCount; j++){
                    // 6.1) authenticate the sector
                    auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
                    if(auth){
                        // 6.2) In each sector - get the block count
                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for(int i = 0; i < bCount; i++){
                            bIndex = mfc.sectorToBlock(j);
                            // 6.3) Read the block
                            data = mfc.readBlock(bIndex);
                            // 7) Convert the data into a string from Hex format.
                            Log.i("TAG", data.toString());
                            bIndex++;
                        }
                    }else{ // Authentication failed - Handle it

                    }
                }
            }catch (IOException e) {
                Log.e("TAG", e.getLocalizedMessage());
            }
        }}// End of method

        @Override
        public void onResume() {
            super.onResume();
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }

        @Override
        public void onNewIntent(Intent intent) {
            Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
            resolveIntent(intent);
        }

        @Override
        public void onPause() {
            super.onPause();
            mAdapter.disableForegroundDispatch(this);
        }


}


