package com.noble.bikeshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DifferentBikeActivity extends AppCompatActivity {

    private static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.bike_type";
    private static final String EXTRA_NEW_BIKE_TYPE = "com.noble.bikeshare.new_bike_type";
    private static final String EXTRA_NEW_BIKE_ID = "com.noble.bikeshare.new_bike_id";

    private static final int REQUEST_CODE_OTHER_BIKES = 0;

    private Button mCategory1;
    private Button mCategory2;

    private String mBikeType;
    private int mId;
    private String mCat1Name;
    private String mCat2Name;

    public static Intent newIntent(Context packageContext, String bikeType) {
        Intent i = new Intent(packageContext, DifferentBikeActivity.class);
        i.putExtra(EXTRA_BIKE_TYPE, bikeType);
        return i;
    }

    private void setCategories(String bikeType) {
        if (bikeType.equals("Generic Bike")) {
            mCat1Name = "Errand Bike";
            mCat2Name = "Road Bike";
        } else if (bikeType.equals("Errand Bike")) {
            mCat1Name = "Generic Bike";
            mCat2Name = "Road Bike";
        } else {
            mCat1Name = "Generic Bike";
            mCat2Name = "Errand Bike";
        }
    }

    private void setBikeOptions(String bikeType, int id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_BIKE_TYPE, bikeType);
        data.putExtra(EXTRA_NEW_BIKE_ID, id);
        setResult(RESULT_OK, data);
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
        setContentView(R.layout.activity_different_bike);

        mBikeType = getIntent().getStringExtra(EXTRA_BIKE_TYPE);
        setCategories(mBikeType);

        mCategory1 = (Button) findViewById(R.id.different_category1_button);
        mCategory1.setText(mCat1Name);
        mCategory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start OtherBikesActivity for first category
                mBikeType = mCat1Name;
                Intent i = OtherBikesActivity.newIntent(DifferentBikeActivity.this, mCat1Name);
                startActivityForResult(i, REQUEST_CODE_OTHER_BIKES);
            }
        });

        mCategory2 = (Button) findViewById(R.id.different_category2_button);
        mCategory2.setText(mCat2Name);
        mCategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start OtherBikesActivity for second category
                mBikeType = mCat2Name;
                Intent i = OtherBikesActivity.newIntent(DifferentBikeActivity.this, mCat2Name);
                startActivityForResult(i, REQUEST_CODE_OTHER_BIKES);
            }
        });

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
        }
    }
}
