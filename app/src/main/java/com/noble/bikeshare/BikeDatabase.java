package com.noble.bikeshare;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.noble.backend.errandBikeApi.ErrandBikeApi;
import com.noble.backend.errandBikeApi.model.ErrandBike;
import com.noble.backend.genericBikeApi.GenericBikeApi;
import com.noble.backend.genericBikeApi.model.GenericBike;
import com.noble.backend.roadBikeApi.RoadBikeApi;
import com.noble.backend.roadBikeApi.model.RoadBike;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 3/18/16.
 */
public class BikeDatabase {
    private static BikeDatabase sBikeDatabase;

    private static GenericBikeApi sGenericBikeService = null;
    private static ErrandBikeApi sErrandBikeService = null;
    private static RoadBikeApi sRoadBikeService = null;

    private int mGenericQuantity;
    private int mGenericAvailable;

    private int mErrandQuantity;
    private int mErrandAvailable;

    private int mRoadQuantity;
    private int mRoadAvailable;

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

        new GetGenericBikesAsyncTask(context).execute();

        new GetErrandBikesAsyncTask(context).execute();

        new GetRoadBikesAsyncTask(context).execute();
    }

    public void updateBikeLists(Context context) {
        new GetGenericBikesAsyncTask(context).execute();

        new GetErrandBikesAsyncTask(context).execute();

        new GetRoadBikesAsyncTask(context).execute();
    }

    public List<GenericBike> getGenericBikes() {
        return mGenericBikes;
    }

    public List<ErrandBike> getErrandBikes() {
        return mErrandBikes;
    }

    public List<RoadBike> getRoadBikes() {
        return mRoadBikes;
    }

    public GenericBike getGenericBike(int id) {
        for (GenericBike bike : mGenericBikes) {
            if (bike.getId().equals(new Long(id))) {
                return bike;
            }
        }
        return null;
    }

    public ErrandBike getErrandBike(int id) {
        for (ErrandBike bike : mErrandBikes) {
            if (bike.getId().equals(new Long(id))) {
                return bike;
            }
        }
        return null;
    }

    public RoadBike getRoadBike(int id) {
        for (RoadBike bike : mRoadBikes) {
            if (bike.getId().equals(new Long(id))) {
                return bike;
            }
        }
        return null;
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
        Context context;

        public GetGenericBikesAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected List<com.noble.backend.genericBikeApi.model.GenericBike> doInBackground(Void... params) {
            if (sGenericBikeService == null) {
                GenericBikeApi.Builder builder = new GenericBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
                sGenericBikeService = builder.build();
            }
            try {
                return sGenericBikeService.list().execute().getItems();
            } catch (IOException e) { }
            return null;
        }
        @Override
        protected void onPostExecute(List<com.noble.backend.genericBikeApi.model.GenericBike> resultList) {
            mGenericBikes = new ArrayList<>(resultList);
            mGenericQuantity = mGenericBikes.size();
            int count = 0;
            for (GenericBike bike : mGenericBikes) {
                if (bike.getAtStation()) {
                    count++;
                }
            }
            mGenericAvailable = count;

            if (context.getClass().getSimpleName().equals("StationActivity")) {
                StationActivity activity = (StationActivity) context;

                // update StationActivity's Availability views
                activity.updateGenericAvailability(mGenericAvailable);

                //update StationActivity's bike lists
                activity.setGenericBikes();
            } else if (context.getClass().getSimpleName().equals("ReserveActivity")) {
                ReserveActivity activity = (ReserveActivity) context;

                //update ReserveActivity's bike lists
                activity.setGenericBikes();
            }
        }
    }

    private class GetErrandBikesAsyncTask extends AsyncTask<Void, Void, List<com.noble.backend.errandBikeApi.model.ErrandBike> > {
        Context context;

        public GetErrandBikesAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected List<com.noble.backend.errandBikeApi.model.ErrandBike> doInBackground(Void... params) {
            if (sErrandBikeService == null) {
                ErrandBikeApi.Builder builder = new ErrandBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
                sErrandBikeService = builder.build();
            }
            try {
                return sErrandBikeService.list().execute().getItems();
            } catch (IOException e) { }
            return null;
        }
        @Override
        protected void onPostExecute(List<com.noble.backend.errandBikeApi.model.ErrandBike> resultList) {
            mErrandBikes = new ArrayList<>(resultList);
            mErrandQuantity = mErrandBikes.size();
            int count = 0;
            for (ErrandBike bike : mErrandBikes) {
                if (bike.getAtStation()) {
                    count++;
                }
            }
            mErrandAvailable = count;

            if (context.getClass().getSimpleName().equals("StationActivity")) {
                StationActivity activity = (StationActivity) context;

                // update StationActivity's Availability views
                activity.updateErrandAvailability(mErrandAvailable);

                // update StationActivity's bike lists
                activity.setErrandBikes();
            } else if (context.getClass().getSimpleName().equals("ReserveActivity")) {
                ReserveActivity activity = (ReserveActivity) context;

                // update ReserveActivity's bike lists
                activity.setErrandBikes();
            }
        }
    }

    private class GetRoadBikesAsyncTask extends AsyncTask<Void, Void, List<com.noble.backend.roadBikeApi.model.RoadBike> > {
        Context context;

        public GetRoadBikesAsyncTask(Context context) {
            this.context = context;
        }
        @Override
        protected List<com.noble.backend.roadBikeApi.model.RoadBike> doInBackground(Void... params) {
            if (sRoadBikeService == null) {
                RoadBikeApi.Builder builder = new RoadBikeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://banded-coder-125919.appspot.com/_ah/api/");
                sRoadBikeService = builder.build();
            }
            try {
                return sRoadBikeService.list().execute().getItems();
            } catch (IOException e) { }
            return null;
        }
        @Override
        protected void onPostExecute(List<com.noble.backend.roadBikeApi.model.RoadBike> resultList) {
            mRoadBikes = new ArrayList<>(resultList);
            mRoadQuantity = mRoadBikes.size();
            int count = 0;
            for (RoadBike bike : mRoadBikes) {
                if (bike.getAtStation()) {
                    count++;
                }
            }
            mRoadAvailable = count;

            if (context.getClass().getSimpleName().equals("StationActivity")) {
                StationActivity activity = (StationActivity) context;

                // update StationActivity's Availability views
                activity.updateRoadAvailability(mRoadAvailable);

                // update StationActivity's bike lists
                activity.setRoadBikes();
            } else if (context.getClass().getSimpleName().equals("ReserveActivity")) {
                ReserveActivity activity = (ReserveActivity) context;

                // update ReserveActivity's bike lists
                activity.setRoadBikes();
            }
        }
    }
}
