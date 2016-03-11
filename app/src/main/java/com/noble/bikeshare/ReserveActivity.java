package com.noble.bikeshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReserveActivity extends AppCompatActivity {

    private static final String EXTRA_LOCATION = "com.noble.bikeshare.location";
    private static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.biketype";
    private static final String EXTRA_ID = "com.noble.bikeshare.id";

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

    private String mBikeType;
    private int mId;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        mLocation = getIntent().getStringExtra(EXTRA_LOCATION);
        mLocationTextView = (TextView) findViewById(R.id.reserve_location_text_view);
        mLocationTextView.setText(mLocation);

        mBikeType = getIntent().getStringExtra(EXTRA_BIKE_TYPE);
        mId = getIntent().getIntExtra(EXTRA_ID, 0);

        mBikeTypeTextView = (TextView) findViewById(R.id.bike_type_text_view);
        mBikeTypeTextView.setText(mBikeType + " " + mId);

        mUnlockButton = (Button) findViewById(R.id.unlock_bike_button);
        mUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start unlock process
            }
        });
    }

}
