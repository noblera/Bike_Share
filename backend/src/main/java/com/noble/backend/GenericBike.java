package com.noble.backend;

import com.googlecode.objectify.annotation.*;

/**
 * Created by richie on 3/27/16.
 */

@Entity
public class GenericBike {

    @Id Long mId;
    boolean mAtStation;

    public GenericBike() {
        mAtStation = true;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public boolean isAtStation() {
        return mAtStation;
    }

    public void setAtStation(boolean atStation) {
        mAtStation = atStation;
    }
}
