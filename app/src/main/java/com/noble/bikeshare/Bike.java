package com.noble.bikeshare;

/**
 * Created by richie on 3/9/16.
 */
public class Bike {

    private String mBikeType;
    private int mId;
    private boolean mAtStation;

    public Bike(String bikeType, int id) {
        mBikeType = bikeType;
        mId = id;
        mAtStation = true;
    }

    public String getBikeType() {
        return mBikeType;
    }

    public void setBikeType(String bikeType) {
        mBikeType = bikeType;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean isAtStation() {
        return mAtStation;
    }

    public void setAtStation(boolean value) {
        mAtStation = value;
    }
}
