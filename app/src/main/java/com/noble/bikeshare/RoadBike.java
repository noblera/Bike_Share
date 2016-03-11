package com.noble.bikeshare;

/**
 * Created by richie on 3/9/16.
 */
public class RoadBike extends Bike {
    static int sRoadQuantity = 0;
    static int sRoadAvailable = sRoadQuantity;

    public RoadBike() {
        setBikeType(R.string.road_type);
        sRoadQuantity = sRoadQuantity + 1;
        sRoadAvailable = sRoadAvailable + 1;
        setId(sRoadQuantity);
        setReserved(false);
    }
}
