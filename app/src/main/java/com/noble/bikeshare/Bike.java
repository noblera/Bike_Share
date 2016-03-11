package com.noble.bikeshare;

/**
 * Created by richie on 3/9/16.
 */
public abstract class Bike {
    private int mBikeType;
    private int mId;
    private boolean mReserved;

    public int getBikeType() {
        return mBikeType;
    }

    public void setBikeType(int bikeType) {
        mBikeType = bikeType;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean isReserved() {
        return mReserved;
    }

    public void setReserved(boolean value) {
        mReserved = value;
    }
}
