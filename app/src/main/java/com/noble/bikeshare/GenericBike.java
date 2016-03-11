package com.noble.bikeshare;

/**
 * Created by richie on 3/9/16.
 */
public class GenericBike extends Bike {
    static int sGenericQuantity = 0;
    static int sGenericAvailable = sGenericQuantity;

    public GenericBike() {
        setBikeType(R.string.generic_type);
        sGenericQuantity = sGenericQuantity + 1;
        sGenericAvailable = sGenericAvailable + 1;
        setId(sGenericQuantity);
        setReserved(false);
    }
}
