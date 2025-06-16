package com.onewaytripcalltaxi.driver.interfaces;

import android.location.Location;

/**
 * Created by developer on 28/9/17.
 */

public interface StreetPickupInterface {
    void updateFare(String distanceFare, Location latLng);
}
