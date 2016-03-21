package com.noble.bikeshare;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

public class OtherBikesActivity extends SingleFragmentActivity {

    public static final String EXTRA_BIKE_TYPE = "com.noble.bikeshare.bike_type";
    public static final String EXTRA_BIKE_ID = "com.noble.bikeshare.bike_id";
    public static final String EXTRA_NEW_BIKE_ID = "com.noble.bikeshare.new_bike_id";

    private int mId;

    public static Intent newIntent(Context packageContext, String bikeType) {
        Intent intent = new Intent(packageContext, OtherBikesActivity.class);
        intent.putExtra(EXTRA_BIKE_TYPE, bikeType);

        return intent;
    }

    public void updateBikeId(int id) {
        mId = id;
        //Toast.makeText(this, "OtherBikesActivity: " + mId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Fragment createFragment() {
        String bikeType = getIntent().getStringExtra(EXTRA_BIKE_TYPE);
        mId = getIntent().getIntExtra(EXTRA_BIKE_ID, 0);
        return OtherBikesFragment.newInstance(bikeType);
    }

    public static int newBikeId(Intent result) {
        return result.getIntExtra(EXTRA_NEW_BIKE_ID, 0);
    }

    public void setBikeOptions(int id) {
        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_BIKE_ID, id);
        setResult(RESULT_OK, data);
    }
}
