package com.noble.bikeshare;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StationActivity extends AppCompatActivity {

    private static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.bike_type";

    private Button mGenericButton;
    private Button mErrandButton;
    private Button mRoadButton;
    private Button mGenericReserve;
    private Button mErrandReserve;
    private Button mRoadReserve;
    private TextView mStationLocation;
    private View mGenericAvailabilityBar;
    private View mErrandAvailabilityBar;
    private View mRoadAvailabilityBar;

    private Boolean mBikeReserved = false;
    private int mReservedId;
    private ArrayList<Bike> mDatabase;

    ArrayList<Bike> createDatabase() {
        ArrayList<Bike> db = new ArrayList();
        for (int i=0; i<10; i++) {
            db.add(new GenericBike());
        }
        for (int i=0; i<10; i++) {
            db.add(new ErrandBike());
        }
        for (int i=0; i<10; i++) {
            db.add(new RoadBike());
        }

        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        // mock database of bikes
        mDatabase = createDatabase();


        mStationLocation = (TextView) findViewById(R.id.location_text_view);
        mStationLocation.setText("Sadler Center");

        // Set up availability bars
        mGenericAvailabilityBar = findViewById(R.id.generic_availability_bar);
        if (GenericBike.sGenericAvailable >= 6) {
            mGenericAvailabilityBar.setBackgroundColor(Color.GREEN);
        } else if (GenericBike.sGenericAvailable > 0 && GenericBike.sGenericAvailable <= 5) {
            mGenericAvailabilityBar.setBackgroundColor(Color.RED);
        } else {
            mGenericAvailabilityBar.setBackgroundColor(Color.BLACK);
        }

        mErrandAvailabilityBar = findViewById(R.id.errand_availability_bar);
        if (ErrandBike.sErrandAvailable >= 6) {
            mErrandAvailabilityBar.setBackgroundColor(Color.GREEN);
        } else if (ErrandBike.sErrandAvailable > 0 && ErrandBike.sErrandAvailable <= 5) {
            mErrandAvailabilityBar.setBackgroundColor(Color.RED);
        } else {
            mErrandAvailabilityBar.setBackgroundColor(Color.BLACK);
        }

        mRoadAvailabilityBar = findViewById(R.id.road_availability_bar);
        if (RoadBike.sRoadAvailable >= 6) {
            mRoadAvailabilityBar.setBackgroundColor(Color.GREEN);
        } else if (RoadBike.sRoadAvailable > 0 && RoadBike.sRoadAvailable <= 5) {
            mRoadAvailabilityBar.setBackgroundColor(Color.RED);
        } else {
            mRoadAvailabilityBar.setBackgroundColor(Color.BLACK);
        }

        mGenericButton = (Button) findViewById(R.id.generic_button);
        mGenericButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved && mGenericReserve.getText().equals("Reserved")) {
                    // start reserve activity for generic bike
                    Intent i = ReserveActivity.newIntent(StationActivity.this, mStationLocation.getText().toString(), "Generic Bike", mReservedId);
                    startActivity(i);
                } else {
                    Toast.makeText(StationActivity.this, R.string.not_reserved_yet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGenericReserve = (Button) findViewById(R.id.generic_reserve_button);
        mGenericReserve.setText("Reserve");
        mGenericReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved) {
                    Toast.makeText(StationActivity.this, R.string.reserved_toast, Toast.LENGTH_SHORT).show();
                } else if (GenericBike.sGenericAvailable == 0) {
                    Toast.makeText(StationActivity.this, R.string.unavailable_toast, Toast.LENGTH_SHORT).show();
                } else {
                    GenericBike.sGenericAvailable = GenericBike.sGenericAvailable - 1;
                    mGenericReserve.setText("Reserved");
                    mBikeReserved = true;

                    // Update availability bar color
                    if (GenericBike.sGenericAvailable <= 5 && GenericBike.sGenericAvailable > 0) {
                        mGenericAvailabilityBar.setBackgroundColor(Color.RED);
                    } else if (GenericBike.sGenericAvailable == 0) {
                        mGenericAvailabilityBar.setBackgroundColor(Color.BLACK);
                    }

                    // Find a generic bike to reserve
                    for (int i = 0; i < mDatabase.size(); i++) {
                        if (mDatabase.get(i).getBikeType() == R.string.generic_type && !mDatabase.get(i).isReserved()) {
                            mDatabase.get(i).setReserved(true);
                            mReservedId = mDatabase.get(i).getId();
                            break;
                        }
                    }
                }
            }
        });

        mErrandButton = (Button) findViewById(R.id.errand_button);
        mErrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved && mErrandReserve.getText().equals("Reserved")) {
                    Intent i = ReserveActivity.newIntent(StationActivity.this, mStationLocation.getText().toString(), "Errand Bike", mReservedId);
                    startActivity(i);
                }
                else {
                    Toast.makeText(StationActivity.this, R.string.not_reserved_yet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mErrandReserve = (Button) findViewById(R.id.errand_reserve_button);
        mErrandReserve.setText("Reserve");
        mErrandReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved) {
                    Toast.makeText(StationActivity.this, R.string.reserved_toast, Toast.LENGTH_SHORT).show();
                } else if (ErrandBike.sErrandAvailable == 0) {
                    Toast.makeText(StationActivity.this, R.string.unavailable_toast, Toast.LENGTH_SHORT).show();
                } else {
                    ErrandBike.sErrandAvailable = ErrandBike.sErrandAvailable - 1;
                    mErrandReserve.setText("Reserved");
                    mBikeReserved = true;

                    // Update errand availability bar color
                    if (ErrandBike.sErrandAvailable <= 5 && ErrandBike.sErrandAvailable > 0) {
                        mErrandAvailabilityBar.setBackgroundColor(Color.RED);
                    } else if (ErrandBike.sErrandAvailable == 0) {
                        mErrandAvailabilityBar.setBackgroundColor(Color.BLACK);
                    }

                    // Find an errand bike to reserve
                    for (int i = 0; i < mDatabase.size(); i++) {
                        if (mDatabase.get(i).getBikeType() == R.string.errand_type && !mDatabase.get(i).isReserved()) {
                            mDatabase.get(i).setReserved(true);
                            mReservedId = mDatabase.get(i).getId();
                            break;
                        }
                    }
                }
            }
        });

        mRoadButton = (Button) findViewById(R.id.road_button);
        mRoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved && mRoadReserve.getText().equals("Reserved")) {
                    Intent i = ReserveActivity.newIntent(StationActivity.this, mStationLocation.getText().toString(), "Road Bike", mReservedId);
                    startActivity(i);
                } else {
                    Toast.makeText(StationActivity.this, R.string.not_reserved_yet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRoadReserve = (Button) findViewById(R.id.road_reserve_button);
        mRoadReserve.setText("Reserve");
        mRoadReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved) {
                    Toast.makeText(StationActivity.this, R.string.reserved_toast, Toast.LENGTH_SHORT).show();
                } else if (RoadBike.sRoadAvailable == 0) {
                    Toast.makeText(StationActivity.this, R.string.unavailable_toast, Toast.LENGTH_SHORT).show();
                } else {
                    RoadBike.sRoadAvailable = RoadBike.sRoadAvailable - 1;
                    mRoadReserve.setText("Reserved");
                    mBikeReserved = true;

                    // Update road availability bar color
                    if (RoadBike.sRoadAvailable <= 5 && RoadBike.sRoadAvailable > 0) {
                        mRoadAvailabilityBar.setBackgroundColor(Color.RED);
                    } else if (RoadBike.sRoadAvailable == 0) {
                        mRoadAvailabilityBar.setBackgroundColor(Color.BLACK);
                    }

                    // Find a road bike to reserve
                    for (int i = 0; i < mDatabase.size(); i++) {
                        if (mDatabase.get(i).getBikeType() == R.string.road_type && !mDatabase.get(i).isReserved()) {
                            mDatabase.get(i).setReserved(true);
                            mReservedId = mDatabase.get(i).getId();
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // clean up static variables

        GenericBike.sGenericQuantity = 0;
        GenericBike.sGenericAvailable = 0;

        ErrandBike.sErrandQuantity = 0;
        ErrandBike.sErrandAvailable = 0;

        RoadBike.sRoadQuantity = 0;
        RoadBike.sRoadAvailable = 0;
    }
}
