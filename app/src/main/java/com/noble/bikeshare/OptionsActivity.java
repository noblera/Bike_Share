package com.noble.bikeshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {

    private static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.bike_type";
    private static final String EXTRA_BIKE_ID = "com.noble.bikeshare.id";
    private static final String EXTRA_NEW_BIKE_TYPE = "com.noble.bikeshare.new_bike_type";
    private static final String EXTRA_NEW_BIKE_ID = "com.noble.bikeshare.new_bike_id";

    private static final int REQUEST_CODE_OTHER_BIKES = 0;
    private static final int REQUEST_CODE_DIFFERENT_BIKE = 1;

    private Button mOtherBikesButton;
    private Button mDifferentBikeButton;

    private String mBikeType;
    private int mId;

    public static Intent newIntent(Context packageContext, String bikeType, int id) {
        Intent i = new Intent(packageContext, OptionsActivity.class);
        i.putExtra(EXTRA_BIKE_TYPE, bikeType);
        i.putExtra(EXTRA_BIKE_ID, id);

        return i;
    }

    public static String newBikeType(Intent result) {
        return result.getStringExtra(EXTRA_NEW_BIKE_TYPE);
    }

    public static int newBikeId(Intent result) {
        return result.getIntExtra(EXTRA_NEW_BIKE_ID, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mBikeType = getIntent().getStringExtra(EXTRA_BIKE_TYPE);
        mId = getIntent().getIntExtra(EXTRA_BIKE_ID, 0);

        mOtherBikesButton = (Button) findViewById(R.id.other_bikes_button);
        mOtherBikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = OtherBikesActivity.newIntent(OptionsActivity.this, mBikeType);
                startActivityForResult(i, REQUEST_CODE_OTHER_BIKES);
            }
        });

        mDifferentBikeButton = (Button) findViewById(R.id.different_bike_button);
        mDifferentBikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start different bike category activity
                Intent i = DifferentBikeActivity.newIntent(OptionsActivity.this, mBikeType);
                startActivityForResult(i, REQUEST_CODE_DIFFERENT_BIKE);
            }
        });
    }

    private void setBikeOptions(String bikeType, int id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_BIKE_TYPE, bikeType);
        data.putExtra(EXTRA_NEW_BIKE_ID, id);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_OTHER_BIKES) {
            if (data == null) {
                return;
            }
            mId = OtherBikesActivity.newBikeId(data);
            setBikeOptions(mBikeType, mId);
            finish();
        } else if (requestCode == REQUEST_CODE_DIFFERENT_BIKE) {
            if (data == null) {
                return;
            }
            mBikeType = DifferentBikeActivity.newBikeType(data);
            mId = DifferentBikeActivity.newBikeId(data);
            setBikeOptions(mBikeType, mId);
            finish();
        }
    }
}
