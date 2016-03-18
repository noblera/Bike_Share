package com.noble.bikeshare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ReserveActivity extends AppCompatActivity {
    private static final String EXTRA_LOCATION = "com.noble.bikeshare.location";
    private static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.biketype";
    private static final String EXTRA_ID = "com.noble.bikeshare.id";
    private static final String EXTRA_BIKE_RETURNED = "com.noble.bikeshare.bike_returned";

    private final String MY_UUID = "c899f350-eab9-11e5-a837-0800200c9a66";

    public static Intent newIntent(Context packageContext, String location, String bikeType, int id) {
        Intent i = new Intent(packageContext, ReserveActivity.class);
        i.putExtra(EXTRA_LOCATION, location);
        i.putExtra(EXTRA_BIKE_TYPE, bikeType);
        i.putExtra(EXTRA_ID, id);

        return i;
    }

    private Button mUnlockButton;
    private Button mMoreOptionsButton;

    private TextView mLocationTextView;
    private TextView mBikeTypeTextView;

    private BikeDatabase mDatabase;
    private List<Bike> mBikes;

    private String mBikeType;
    private int mId;
    private String mLocation;

    private BluetoothAdapter mBA;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;

    private InputStream mInStream;
    private OutputStream mOutStream;

    private boolean mBikeReturned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        mDatabase = BikeDatabase.get(this);
        mBikes = mDatabase.getBikes();

        mLocation = getIntent().getStringExtra(EXTRA_LOCATION);
        mLocationTextView = (TextView) findViewById(R.id.reserve_location_text_view);
        mLocationTextView.setText(mLocation);

        mBikeType = getIntent().getStringExtra(EXTRA_BIKE_TYPE);
        mId = getIntent().getIntExtra(EXTRA_ID, 0);

        if (mDatabase.getBike(mBikeType, mId).isAtStation()) {
            mBikeReturned = true;
        } else {
            mBikeReturned = false;
        }

        mBikeTypeTextView = (TextView) findViewById(R.id.bike_type_text_view);
        mBikeTypeTextView.setText(mBikeType + " " + mId);

        mUnlockButton = (Button) findViewById(R.id.unlock_bike_button);
        if (mBikeReturned) {
            mUnlockButton.setText("Unlock");
        } else {
            mUnlockButton.setText("Lock");
        }
        mUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start unlock process
                mBA = BluetoothAdapter.getDefaultAdapter();
                if (mBA == null) {
                    Toast.makeText(ReserveActivity.this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBA.isEnabled()) {
                        Toast.makeText(ReserveActivity.this, "Turning on bluetooth", Toast.LENGTH_SHORT).show();
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnOn, 0);
                    } else {
                        Set<BluetoothDevice> pairedDevices = mBA.getBondedDevices();
                        if (pairedDevices.size() > 0) {
                            for (BluetoothDevice device : pairedDevices) {
                                if (device.getName().equals("Nexus 4")) {
                                    mDevice = device;
                                    break;
                                }
                            }
                        }
                        // set up connection to lock
                        try {
                            mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                        } catch (IOException e) { }

                        try {
                            mSocket.connect();
                        } catch (IOException connectException) {
                            try {
                                mSocket.close();
                            } catch (IOException closeException) { }
                        }
                        // connected, attempt to send password for verification
                        try {
                            mInStream = mSocket.getInputStream();
                            mOutStream = mSocket.getOutputStream();
                        } catch (IOException e) { }

                        if (mUnlockButton.getText().toString().equals("Unlock")) {
                            String tmp_pass = "test";
                            byte[] byte_pass = tmp_pass.getBytes();
                            try {
                                mOutStream.write(byte_pass);
                            } catch (IOException e) { }
                            // sent password, wait for confirmation
                            byte[] buffer = new byte[1024];
                            int bytes; // number of bytes returned
                            try {
                                bytes = mInStream.read(buffer);
                            } catch (IOException e) { }
                            String s = new String(buffer, Charset.forName("UTF-8"));
                            String answerYes = "y";
                            boolean unlocked = true;
                            byte[] byteYes = answerYes.getBytes();
                            for (int i = 0; i < byteYes.length; i++) {
                                if (byteYes[i] != buffer[i]) {
                                    Toast.makeText(ReserveActivity.this, "Failed to unlock bike", Toast.LENGTH_SHORT).show();
                                    unlocked = false;
                                    break;
                                }
                            }
                            if (unlocked) {
                                // remove unlocked bike from list of bikes at station
                                mBikeReturned = false;
                                mDatabase.getBike(mBikeType, mId).setAtStation(false);
                                Toast.makeText(ReserveActivity.this, "Bike Unlocked", Toast.LENGTH_SHORT).show();
                                mUnlockButton.setText(R.string.lock_button);
                            }
                        } else {
                            // checking if bike is properly "locked" again
                            String query = "q";
                            byte[] queryByte = query.getBytes();

                            try {
                                mOutStream.write(queryByte);
                            } catch (IOException e) { }

                            byte[] buffer = new byte[1024];
                            int bytes;
                            try {
                                bytes = mInStream.read(buffer);
                            } catch (IOException e) { }

                            String queryYes = "y";
                            byte[] queryYesByte = queryYes.getBytes();
                            boolean locked = true;
                            for (int i=0; i<queryYesByte.length; i++) {
                                if (queryYesByte[i] != buffer[i]) {
                                    Toast.makeText(ReserveActivity.this, "Bike is not locked", Toast.LENGTH_SHORT).show();
                                    locked = false;
                                    break;
                                }
                            }
                            if (locked) {
                                Toast.makeText(ReserveActivity.this, "Bike is locked", Toast.LENGTH_SHORT).show();
                                mUnlockButton.setText(R.string.unlock_button);
                                mDatabase.getBike(mBikeType, mId).setAtStation(true);
                                setBikeReturnedResult(true);
                                mBikeReturned = true;
                            }
                        }
                        try {
                            mSocket.close();
                        } catch (IOException e) { }

                        if (mBikeReturned) {
                            finish();
                        }
                    }
                }
            }
        });

        mMoreOptionsButton = (Button) findViewById(R.id.reserve_more_options_button);
        mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReserveActivity.this, OptionsActivity.class);
                startActivity(i);
            }
        });
    }

    private void setBikeReturnedResult(boolean isBikeReturned) {
        Intent data = new Intent();
        data.putExtra(EXTRA_BIKE_RETURNED, isBikeReturned);
        setResult(RESULT_OK, data);
    }

    public static boolean wasBikeReturned(Intent result) {
        return result.getBooleanExtra(EXTRA_BIKE_RETURNED, false);
    }
}
