package com.noble.bikeshare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.noble.backend.genericBikeApi.GenericBikeApi;
import com.noble.backend.genericBikeApi.model.GenericBike;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 3/18/16.
 */
public class BikeDatabase {
    private static BikeDatabase sBikeDatabase;

    private int mGenericQuantity;
    private int mGenericAvailable;

    private int mErrandQuantity;
    private int mErrandAvailable;

    private int mRoadQuantity;
    private int mRoadAvailable;

    private List<Bike> mBikes;
    private List<com.noble.backend.genericBikeApi.model.GenericBike> mGenericBikes;
    private List<com.noble.backend.errandBikeApi.model.ErrandBike> mErrandBikes;
    private List<com.noble.backend.roadBikeApi.model.RoadBike> mRoadBikes;

    public static BikeDatabase get(Context context) {
        if (sBikeDatabase == null) {
            sBikeDatabase = new BikeDatabase(context);
        }
        return sBikeDatabase;
    }

    private BikeDatabase(Context context) {

        mBikes = new ArrayList<>();
        new GetGenericBikesAsyncTask().execute();
        if (mGenericBikes == null) { // we need to preload the database still
            Toast.makeText(context, "got here", Toast.LENGTH_SHORT).show();
            for (int i=0; i<10; i++) {
                GenericBike bike = new GenericBike();
                bike.setId(new Long(i+1));
                new AddGenericBikeAsyncTask().execute(bike);
            }
        }
        mGenericQuantity = 0;
        mGenericAvailable = 0;

        mErrandBikes = new ArrayList<>();
        mErrandQuantity = 0;
        mErrandAvailable = 0;

        mRoadBikes = new ArrayList<>();
        mRoadQuantity = 0;
        mRoadAvailable = 0;

        for (int i=0; i<10; i++) {
            mGenericQuantity++;
            mBikes.add(new Bike("Generic Bike", mGenericQuantity));
        }
        mGenericAvailable = mGenericQuantity;
        for (int i=0; i<10; i++) {
            mErrandQuantity++;
            mBikes.add(new Bike("Errand Bike", mErrandQuantity));
        }
        mErrandAvailable = mErrandQuantity;
        for (int i=0; i<10; i++) {
            mRoadQuantity++;
            mBikes.add(new Bike("Road Bike", mRoadQuantity));
        }
        mRoadAvailable = mRoadQuantity;
    }

    public List<Bike> getBikes() {
        return mBikes;
    }

    public Bike getBike(String bikeType, int id) {
        for (Bike bike : mBikes) {
            if (bike.getBikeType().equals(bikeType) && bike.getId() == id) {
                return bike;
            }
        }
        return null;
    }

    public void addBike(String bikeType) {
        int id;
        if (bikeType.equals("Generic Bike")) {
            mGenericQuantity++;
            id = mGenericQuantity;
            mGenericAvailable++;
        } else if (bikeType.equals("Errand Bike")) {
            mErrandQuantity++;
            id = mErrandQuantity;
            mErrandAvailable++;
        } else {
            mRoadQuantity++;
            id = mRoadQuantity;
            mRoadAvailable++;
        }
        Bike bike = new Bike(bikeType, id);
        mBikes.add(bike);
    }

    public int getGenericQuantity() {
        return mGenericQuantity;
    }

    public void setGenericQuantity(int genericQuantity) {
        mGenericQuantity = genericQuantity;
    }

    public int getGenericAvailable() {
        return mGenericAvailable;
    }

    public void setGenericAvailable(int genericAvailable) {
        mGenericAvailable = genericAvailable;
    }

    public int getErrandQuantity() {
        return mErrandQuantity;
    }

    public void setErrandQuantity(int errandQuantity) {
        mErrandQuantity = errandQuantity;
    }

    public int getErrandAvailable() {
        return mErrandAvailable;
    }

    public void setErrandAvailable(int errandAvailable) {
        mErrandAvailable = errandAvailable;
    }

    public int getRoadQuantity() {
        return mRoadQuantity;
    }

    public void setRoadQuantity(int roadQuantity) {
        mRoadQuantity = roadQuantity;
    }

    public int getRoadAvailable() {
        return mRoadAvailable;
    }

    public void setRoadAvailable(int roadAvailable) {
        mRoadAvailable = roadAvailable;
    }

    private class GetGenericBikesAsyncTask extends AsyncTask<Void, Void, List<com.noble.backend.genericBikeApi.model.GenericBike> > {
        @Override
        protected List<com.noble.backend.genericBikeApi.model.GenericBike> doInBackground(Void... params) {
            GenericBikeApi.Builder builder = new GenericBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
            GenericBikeApi service = builder.build();
            try {
                return service.list().execute().getItems();
            } catch (IOException e) { }
            return null;
        }
        @Override
        protected void onPostExecute(List<com.noble.backend.genericBikeApi.model.GenericBike> resultList) {
            mGenericBikes = resultList;
        }
    }

    private class AddGenericBikeAsyncTask extends AsyncTask<GenericBike, Void, Void> {
        @Override
        protected Void doInBackground(GenericBike... params) {
            GenericBikeApi.Builder builder = new GenericBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
            GenericBikeApi service = builder.build();
            try {
                GenericBike bike = params[0];
                Log.e("Debug", "Inserting bike...");
                service.insert(bike);
                Log.e("Debug", "Done inserting bike.");
            } catch (IOException e) {e.printStackTrace(); }
            return null;
        }
    }
}
