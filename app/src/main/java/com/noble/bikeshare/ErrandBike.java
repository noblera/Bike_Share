package com.noble.bikeshare;

/**
 * Created by richie on 3/9/16.
 */
public class ErrandBike extends Bike {
    static int sErrandQuantity = 0;
    static int sErrandAvailable = sErrandQuantity;

    public ErrandBike() {
        setBikeType(R.string.errand_type);
        sErrandQuantity = sErrandQuantity + 1;
        sErrandAvailable = sErrandAvailable + 1;
        setId(sErrandQuantity);
        setReserved(false);
    }
}
