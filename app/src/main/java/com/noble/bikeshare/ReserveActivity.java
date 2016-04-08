package com.noble.bikeshare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.noble.backend.errandBikeApi.ErrandBikeApi;
import com.noble.backend.errandBikeApi.model.ErrandBike;
import com.noble.backend.genericBikeApi.GenericBikeApi;
import com.noble.backend.genericBikeApi.model.GenericBike;
import com.noble.backend.roadBikeApi.RoadBikeApi;
import com.noble.backend.roadBikeApi.model.RoadBike;

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
    private static final String EXTRA_BIKE_UNLOCKED = "com.noble.bikeshare.bike_unlocked";
    private static final String EXTRA_NEW_BIKE_TYPE = "com.noble.bikeshare.new_bike_type";
    private static final String EXTRA_NEW_BIKE_ID = "com.noble.bikeshare.new_bike_id";

    private static final int REQUEST_CODE_OPTIONS = 0;

    private final String MY_UUID = "c899f350-eab9-11e5-a837-0800200c9a66";

    public static Intent newIntent(Context packageContext, String location, String bikeType, int id) {
        Intent i = new Intent(packageContext, ReserveActivity.class);
        i.putExtra(EXTRA_LOCATION, location);
        i.putExtra(EXTRA_BIKE_TYPE, bikeType);
        i.putExtra(EXTRA_ID, id);

        return i;
    }

    private static GenericBikeApi sGenericBikeService = null;
    private static ErrandBikeApi sErrandBikeService = null;
    private static RoadBikeApi sRoadBikeService = null;

    private Button mUnlockButton;
    private Button mMoreOptionsButton;
    private Button mReturnButton;

    private TextView mLocationTextView;
    private TextView mBikeTypeTextView;

    private ImageView mBikeImage;

    private BikeDatabase mDatabase;
    private List<GenericBike> mGenericBikes;
    private List<ErrandBike> mErrandBikes;
    private List<RoadBike> mRoadBikes;

    private String mBikeType;
    private int mId;
    private String mLocation;

    private BluetoothAdapter mBA;
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;

    private InputStream mInStream;
    private OutputStream mOutStream;

    private boolean mBikeReturned;
    private boolean mBikeUnlocked;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName().equals("Nexus 4")) { // normally pull bike lock name from backend
                    mDevice = device;
                }
            }
        }
    };

    public void setGenericBikes() {
        mGenericBikes = mDatabase.getGenericBikes();
    }

    public void setErrandBikes() {
        mErrandBikes = mDatabase.getErrandBikes();
    }

    public void setRoadBikes() {
        mRoadBikes = mDatabase.getRoadBikes();
    }

    private void updateChosenBike(String bikeType, int id) {
        mBikeType = bikeType;
        mId = id;
        mBikeTypeTextView.setText(mBikeType + " " + mId);

        if (mBikeType.equals("Generic Bike")) {
            mBikeImage.setImageResource(R.drawable.generic_bike);
        } else if (mBikeType.equals("Errand Bike")) {
            mBikeImage.setImageResource(R.drawable.errand_bike);
        } else {
            mBikeImage.setImageResource(R.drawable.road_bike);
        }

        updateBikeStatus(mBikeReturned, mBikeUnlocked, mBikeType, mId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        mDatabase = BikeDatabase.get(this);
        if (mGenericBikes == null) {
            setGenericBikes();
        }
        if (mErrandBikes == null) {
            setErrandBikes();
        }
        if (mRoadBikes == null) {
            setRoadBikes();
        }

        // set up widgets
        mLocation = getIntent().getStringExtra(EXTRA_LOCATION);
        mLocationTextView = (TextView) findViewById(R.id.reserve_location_text_view);
        mLocationTextView.setText(mLocation);

        mBikeType = getIntent().getStringExtra(EXTRA_BIKE_TYPE);
        mId = getIntent().getIntExtra(EXTRA_ID, 0);


        if (mBikeType.equals("Generic Bike")) {
            if (mDatabase.getGenericBike(mId).getAtStation()) {
                mBikeUnlocked = false;
            } else {
                mBikeUnlocked = true;
            }
        } else if (mBikeType.equals("Errand Bike")) {
            if (mDatabase.getErrandBike(mId).getAtStation()) {
                mBikeUnlocked = false;
            } else {
                mBikeUnlocked = true;
            }
        } else {
            if (mDatabase.getRoadBike(mId).getAtStation()) {
                mBikeUnlocked = false;
            } else {
                mBikeUnlocked = true;
            }
        }
        mBikeReturned = false;

        mBikeTypeTextView = (TextView) findViewById(R.id.bike_type_text_view);
        mBikeTypeTextView.setText(mBikeType + " " + mId);

        mBikeImage = (ImageView) findViewById(R.id.bike_image);
        if (mBikeType.equals("Generic Bike")) {
            mBikeImage.setImageResource(R.drawable.generic_bike);
        } else if (mBikeType.equals("Errand Bike")) {
            mBikeImage.setImageResource(R.drawable.errand_bike);
        } else {
            mBikeImage.setImageResource(R.drawable.road_bike);
        }

        // new thread for updating OTPs periodically
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    mDatabase.updateBikeLists(ReserveActivity.this);
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) { }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        mUnlockButton = (Button) findViewById(R.id.unlock_bike_button);
        mUnlockButton.setText("Unlock Bike");

        mReturnButton = (Button) findViewById(R.id.return_bike_button);

        mBA = BluetoothAdapter.getDefaultAdapter();
        mUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start communication process with bike lock
                if (mBA == null) {
                    Toast.makeText(ReserveActivity.this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBA.isEnabled()) {
                        Toast.makeText(ReserveActivity.this, "Turning on bluetooth", Toast.LENGTH_SHORT).show();
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnOn, 0);
                    } else {
                        // check if we're already paired with bike lock
                        if (mDevice == null) {
                            Set<BluetoothDevice> pairedDevices = mBA.getBondedDevices();
                            if (pairedDevices.size() > 0) {
                                for (BluetoothDevice device : pairedDevices) {
                                    if (device != null && device.getName().equals("Nexus 4")) {
                                        mDevice = device;
                                        break;
                                    }
                                }
                            }
                        }
                        if (mDevice == null) {
                            // not paired, need to discover it
                            if (mBA.isDiscovering()) { // cancel old discovery attempts
                                mBA.cancelDiscovery();
                            }
                            if(!mBA.startDiscovery()) {
                            // start a fresh one
                                Toast.makeText(ReserveActivity.this, "false...", Toast.LENGTH_SHORT);
                            }

                            // register a broadcast receiver for discovered devices
                            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                            registerReceiver(mReceiver, filter);
                            while (mBA.isDiscovering()) {
                                continue;
                            }
                        }
                        if (mDevice != null) {
                            // set up connection to lock
                            try {
                                mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                            } catch (IOException e) { }
                            if (mBA.isDiscovering()) {
                                mBA.cancelDiscovery();
                            }
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

                                String otp;
                                if (mBikeType.equals("Generic Bike")) {
                                    otp = mDatabase.getGenericBike(mId).getOtp();
                                } else if (mBikeType.equals("Errand Bike")) {
                                    otp = mDatabase.getErrandBike(mId).getOtp();
                                } else {
                                    otp = mDatabase.getRoadBike(mId).getOtp();
                                }
                                byte[] otpBytes = otp.getBytes();
                                try {
                                    mOutStream.write(otpBytes);
                                } catch (IOException e) { }
                                // sent password, wait for confirmation
                                byte[] buffer = new byte[1024];
                                int bytes; // number of bytes returned
                                try {
                                    bytes = mInStream.read(buffer);
                                } catch (IOException e) { }
                                String answerYes = "y";
                                boolean unlocked = true;
                                byte[] byteYes = answerYes.getBytes();
                                for (int i = 0; i < byteYes.length; i++) {
                                    if (byteYes[i] != buffer[i]) {
                                        // check if bike has already been unlocked
                                        String answerUnlocked = "u";
                                        byte[] byteUnlocked = answerUnlocked.getBytes();
                                        for (int j=0; j<byteYes.length; j++) {
                                            if (byteUnlocked[i] != buffer[i]) {
                                                Toast.makeText(ReserveActivity.this, "Failed to unlock bike", Toast.LENGTH_SHORT).show();
                                                unlocked = false;
                                                break;
                                            }
                                        }
                                        if (unlocked) {
                                            Toast.makeText(ReserveActivity.this, "Bike already unlocked", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                }
                                if (!mBikeUnlocked && unlocked) { // only enter the first time bike is unlocked
                                    // remove unlocked bike from list of bikes at station
                                    mBikeUnlocked = true;
                                    if (mBikeType.equals("Generic Bike")) {
                                        GenericBike bike = mDatabase.getGenericBike(mId).setAtStation(false);
                                        new UpdateGenericBikeAsyncTask().execute(bike);
                                    } else if (mBikeType.equals("Errand Bike")) {
                                        ErrandBike bike = mDatabase.getErrandBike(mId).setAtStation(false);
                                        new UpdateErrandBikeAsyncTask().execute(bike);
                                    } else {
                                        RoadBike bike = mDatabase.getRoadBike(mId).setAtStation(false);
                                        new UpdateRoadBikeAsyncTask().execute(bike);
                                    }
                                    Toast.makeText(ReserveActivity.this, "Bike Unlocked", Toast.LENGTH_SHORT).show();
                                }
                            try {
                                mSocket.close();
                            } catch (IOException e) { }

                            if (mBikeReturned) {
                                finish();
                            }
                        } else {
                            Toast.makeText(ReserveActivity.this, "Bike lock not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBikeUnlocked) {
                    Toast.makeText(ReserveActivity.this, "Bike not unlocked yet", Toast.LENGTH_SHORT).show();
                } else {
                    if (mDevice != null) {
                        // set up connection to lock
                        try {
                            mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                        } catch (IOException e) { }
                        if (mBA.isDiscovering()) {
                            mBA.cancelDiscovery();
                        }
                        try {
                            mSocket.connect();
                        } catch (IOException connectException) {
                            try {
                                mSocket.close();
                            } catch (IOException closeException) { }
                        }

                        // connection made, set up streams
                        try {
                            mInStream = mSocket.getInputStream();
                            mOutStream = mSocket.getOutputStream();
                        } catch (IOException e) { }

                        // query that bike has been locked
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
                        String queryYes = "y"; // expected answer if bike is locked
                        byte[] queryYesByte = queryYes.getBytes();
                        boolean locked = true;
                        for (int i = 0; i < queryYesByte.length; i++) {
                            if (queryYesByte[i] != buffer[i]) {
                                Toast.makeText(ReserveActivity.this, "Bike is not locked", Toast.LENGTH_SHORT).show();
                                locked = false;
                                break;
                            }
                        }
                        if (locked) {
                            if (mBikeType.equals("Generic Bike")) {
                                GenericBike bike = mDatabase.getGenericBike(mId).setAtStation(true);
                                new UpdateGenericBikeAsyncTask().execute(bike);
                            } else if (mBikeType.equals("Errand Bike")) {
                                ErrandBike bike = mDatabase.getErrandBike(mId).setAtStation(true);
                                new UpdateErrandBikeAsyncTask().execute(bike);
                            } else {
                                RoadBike bike = mDatabase.getRoadBike(mId).setAtStation(true);
                                new UpdateRoadBikeAsyncTask().execute(bike);
                            }
                            updateBikeStatus(true, false, mBikeType, mId);
                            mBikeReturned = true;
                            mBikeUnlocked = false;
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
                Intent i = OptionsActivity.newIntent(ReserveActivity.this, mBikeType, mId, mBikeUnlocked);
                startActivityForResult(i, REQUEST_CODE_OPTIONS);
            }
        });
    }

    private void updateBikeStatus(boolean isBikeReturned, boolean isBikeUnlocked, String bikeType, int id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_BIKE_RETURNED, isBikeReturned);
        data.putExtra(EXTRA_BIKE_UNLOCKED, isBikeUnlocked);
        data.putExtra(EXTRA_NEW_BIKE_TYPE, bikeType);
        data.putExtra(EXTRA_NEW_BIKE_ID, id);
        setResult(RESULT_OK, data);
    }

    public static boolean wasBikeReturned(Intent result) {
        return result.getBooleanExtra(EXTRA_BIKE_RETURNED, false);
    }

    public static boolean wasBikeUnlocked(Intent result) {
        return result.getBooleanExtra(EXTRA_BIKE_UNLOCKED, false);
    }

    public static String newBikeType(Intent result) {
        return result.getStringExtra(EXTRA_NEW_BIKE_TYPE);
    }

    public static int newBikeId(Intent result) {
        return result.getIntExtra(EXTRA_NEW_BIKE_ID, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_OPTIONS) {
            if (data == null) {
                return;
            }
            String bikeType = OptionsActivity.newBikeType(data);
            int id = OptionsActivity.newBikeId(data);
            updateChosenBike(bikeType, id);
        }
    }

    private class UpdateGenericBikeAsyncTask extends AsyncTask<GenericBike, Void, Void> {
        @Override
        protected Void doInBackground(GenericBike... params) {
            if (sGenericBikeService == null) {
                GenericBikeApi.Builder builder = new GenericBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
                sGenericBikeService = builder.build();
            }
            try {
                GenericBike bike = params[0];
                Long id = bike.getId();
                sGenericBikeService.update(id, bike).execute();
            } catch (IOException e) { }
            return null;
        }
    }

    private class UpdateErrandBikeAsyncTask extends AsyncTask<ErrandBike, Void, Void> {
        @Override
        protected Void doInBackground(ErrandBike... params) {
            if (sErrandBikeService == null) {
                ErrandBikeApi.Builder builder = new ErrandBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
                sErrandBikeService = builder.build();
            }
            try {
                ErrandBike bike = params[0];
                Long id = bike.getId();
                sErrandBikeService.update(id, bike).execute();
            } catch (IOException e) { }
            return null;
        }
    }

    private class UpdateRoadBikeAsyncTask extends AsyncTask<RoadBike, Void, Void> {
        @Override
        protected Void doInBackground(RoadBike... params) {
            if (sRoadBikeService == null) {
                RoadBikeApi.Builder builder = new RoadBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
                sRoadBikeService = builder.build();
            }
            try {
                RoadBike bike = params[0];
                Long id = bike.getId();
                sRoadBikeService.update(id, bike).execute();
            } catch (IOException e) { }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // do some cleanup
        if (mBA != null && mBA.isDiscovering()) {
            mBA.cancelDiscovery();
        }
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) { }
    }
}
