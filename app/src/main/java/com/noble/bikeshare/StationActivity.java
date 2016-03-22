package com.noble.bikeshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StationActivity extends FragmentActivity {

    private static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.bike_type";
    private static final int REQUEST_CODE_RETURNED = 0;
    private static final String DIALOG_UNRESERVE = "DialogUnreserve";

    private ImageButton mGenericButton;
    private ImageButton mErrandButton;
    private ImageButton mRoadButton;
    private Button mGenericReserve;
    private Button mErrandReserve;
    private Button mRoadReserve;
    private TextView mStationLocation;
    private View mGenericAvailabilityBar;
    private View mErrandAvailabilityBar;
    private View mRoadAvailabilityBar;

    private Boolean mBikeReserved = false;
    private int mReservedId;
    private BikeDatabase mDatabase;
    private List<Bike> mBikes;

    public void updateReserveStatus(boolean bikeReturned) {
        if (bikeReturned) {
            mBikeReserved = false;
            if (mGenericReserve.getText().equals("Reserved")) {
                mGenericReserve.setText("Reserve");
            } else if (mErrandReserve.getText().equals("Reserved")) {
                mErrandReserve.setText("Reserve");
            } else {
                mRoadReserve.setText("Reserve");
            }
        }
    }

    private void updateBikeStatus(boolean bikeReturned, boolean bikeUnlocked, String bikeType, int id) {
        updateReserveStatus(bikeReturned);
        if (bikeReturned) {
            return;
        }

        if (!bikeUnlocked && bikeType.equals("Generic Bike")) {
            mGenericReserve.setText("Reserved");
            mErrandReserve.setText("Reserve");
            mRoadReserve.setText("Reserve");
        } else if (!bikeUnlocked && bikeType.equals("Errand Bike")) {
            mGenericReserve.setText("Reserve");
            mErrandReserve.setText("Reserved");
            mRoadReserve.setText(("Reserve"));
        } else if (!bikeUnlocked && bikeType.equals("Road Bike")) {
            mGenericReserve.setText("Reserve");
            mErrandReserve.setText("Reserve");
            mRoadReserve.setText("Reserved");
        }
        mReservedId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        // mock database of bikes
        mDatabase = BikeDatabase.get(this);
        mBikes = mDatabase.getBikes();

        mStationLocation = (TextView) findViewById(R.id.location_text_view);
        mStationLocation.setText("Sadler Center");

        // Set up availability bars
        mGenericAvailabilityBar = findViewById(R.id.generic_availability_bar);
        if (mDatabase.getGenericAvailable() >= 6) {
            mGenericAvailabilityBar.setBackgroundColor(Color.GREEN);
        } else if (mDatabase.getGenericAvailable() > 0 && mDatabase.getGenericAvailable() <= 5) {
            mGenericAvailabilityBar.setBackgroundColor(Color.RED);
        } else {
            mGenericAvailabilityBar.setBackgroundColor(Color.BLACK);
        }

        mErrandAvailabilityBar = findViewById(R.id.errand_availability_bar);
        if (mDatabase.getErrandAvailable() >= 6) {
            mErrandAvailabilityBar.setBackgroundColor(Color.GREEN);
        } else if (mDatabase.getErrandAvailable() > 0 && mDatabase.getErrandAvailable() <= 5) {
            mErrandAvailabilityBar.setBackgroundColor(Color.RED);
        } else {
            mErrandAvailabilityBar.setBackgroundColor(Color.BLACK);
        }

        mRoadAvailabilityBar = findViewById(R.id.road_availability_bar);
        if (mDatabase.getRoadAvailable() >= 6) {
            mRoadAvailabilityBar.setBackgroundColor(Color.GREEN);
        } else if (mDatabase.getRoadAvailable() > 0 && mDatabase.getRoadAvailable() <= 5) {
            mRoadAvailabilityBar.setBackgroundColor(Color.RED);
        } else {
            mRoadAvailabilityBar.setBackgroundColor(Color.BLACK);
        }

        mGenericButton = (ImageButton) findViewById(R.id.generic_button);
        mGenericButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved && mGenericReserve.getText().equals("Reserved")) {
                    // start reserve activity for generic bike
                    Intent i = ReserveActivity.newIntent(StationActivity.this, mStationLocation.getText().toString(), "Generic Bike", mReservedId);
                    startActivityForResult(i, REQUEST_CODE_RETURNED);
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
                } else if (mDatabase.getGenericAvailable() == 0) {
                    Toast.makeText(StationActivity.this, R.string.unavailable_toast, Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.setGenericAvailable(mDatabase.getGenericAvailable()-1);
                    mGenericReserve.setText("Reserved");
                    mBikeReserved = true;

                    // Update availability bar color
                    if (mDatabase.getGenericAvailable() <= 5 && mDatabase.getGenericAvailable() > 0) {
                        mGenericAvailabilityBar.setBackgroundColor(Color.RED);
                    } else if (mDatabase.getGenericAvailable() == 0) {
                        mGenericAvailabilityBar.setBackgroundColor(Color.BLACK);
                    }

                    // Find a generic bike to reserve
                    for (int i = 0; i < mBikes.size(); i++) {
                        if (mBikes.get(i).getBikeType().equals("Generic Bike") && mBikes.get(i).isAtStation()) {
                            mReservedId = mBikes.get(i).getId();
                            break;
                        }
                    }
                }
            }
        });
        mGenericReserve.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBikeReserved && mGenericReserve.getText().equals("Reserved")) {
                    FragmentManager manager = getSupportFragmentManager();
                    UnreserveBikeFragment dialog = new UnreserveBikeFragment();
                    dialog.show(manager, DIALOG_UNRESERVE);
                    return true;
                }
                return false;
            }
        });

        mErrandButton = (ImageButton) findViewById(R.id.errand_button);
        mErrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved && mErrandReserve.getText().equals("Reserved")) {
                    Intent i = ReserveActivity.newIntent(StationActivity.this, mStationLocation.getText().toString(), "Errand Bike", mReservedId);
                    startActivityForResult(i, REQUEST_CODE_RETURNED);
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
                } else if (mDatabase.getErrandAvailable() == 0) {
                    Toast.makeText(StationActivity.this, R.string.unavailable_toast, Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.setErrandAvailable(mDatabase.getErrandAvailable()-1);
                    mErrandReserve.setText("Reserved");
                    mBikeReserved = true;

                    // Update errand availability bar color
                    if (mDatabase.getErrandAvailable() <= 5 && mDatabase.getErrandAvailable() > 0) {
                        mErrandAvailabilityBar.setBackgroundColor(Color.RED);
                    } else if (mDatabase.getErrandAvailable() == 0) {
                        mErrandAvailabilityBar.setBackgroundColor(Color.BLACK);
                    }

                    // Find an errand bike to reserve
                    for (int i = 0; i < mBikes.size(); i++) {
                        if (mBikes.get(i).getBikeType().equals("Errand Bike") && mBikes.get(i).isAtStation()) {
                            mReservedId = mBikes.get(i).getId();
                            break;
                        }
                    }
                }
            }
        });
        mErrandReserve.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBikeReserved && mErrandReserve.getText().equals("Reserved")) {
                    FragmentManager manager = getSupportFragmentManager();
                    UnreserveBikeFragment dialog = new UnreserveBikeFragment();
                    dialog.show(manager, DIALOG_UNRESERVE);
                    return true;
                }
                return false;
            }
        });

        mRoadButton = (ImageButton) findViewById(R.id.road_button);
        mRoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBikeReserved && mRoadReserve.getText().equals("Reserved")) {
                    Intent i = ReserveActivity.newIntent(StationActivity.this, mStationLocation.getText().toString(), "Road Bike", mReservedId);
                    startActivityForResult(i, REQUEST_CODE_RETURNED);
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
                } else if (mDatabase.getRoadAvailable() == 0) {
                    Toast.makeText(StationActivity.this, R.string.unavailable_toast, Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase.setRoadAvailable(mDatabase.getRoadAvailable()-1);
                    mRoadReserve.setText("Reserved");
                    mBikeReserved = true;

                    // Update road availability bar color
                    if (mDatabase.getRoadAvailable() <= 5 && mDatabase.getRoadAvailable() > 0) {
                        mRoadAvailabilityBar.setBackgroundColor(Color.RED);
                    } else if (mDatabase.getRoadAvailable() == 0) {
                        mRoadAvailabilityBar.setBackgroundColor(Color.BLACK);
                    }

                    // Find a road bike to reserve
                    for (int i = 0; i < mBikes.size(); i++) {
                        if (mBikes.get(i).getBikeType().equals("Road Bike") && mBikes.get(i).isAtStation()) {
                            mReservedId = mBikes.get(i).getId();
                            break;
                        }
                    }
                }
            }
        });
        mRoadReserve.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBikeReserved && mRoadReserve.getText().equals("Reserved")) {
                    FragmentManager manager = getSupportFragmentManager();
                    UnreserveBikeFragment dialog = new UnreserveBikeFragment();
                    dialog.show(manager, DIALOG_UNRESERVE);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_RETURNED) {
            if (data == null) {
                return;
            }
            boolean bikeReturned = ReserveActivity.wasBikeReturned(data);
            boolean bikeUnlocked = ReserveActivity.wasBikeUnlocked(data);
            String bikeType = ReserveActivity.newBikeType(data);
            int id = ReserveActivity.newBikeId(data);
            updateBikeStatus(bikeReturned, bikeUnlocked, bikeType, id);
        }
    }
}
