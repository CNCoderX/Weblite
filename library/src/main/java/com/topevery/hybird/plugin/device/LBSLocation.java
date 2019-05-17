package com.topevery.hybird.plugin.device;

import android.location.Location;

import com.topevery.hybird.utils.Utils;

import java.util.Date;

/**
 * @author wujie
 */
public class LBSLocation {
    public double latitude;
    public double longitude;
    public float speed;
    public float accuracy;
    public double altitude;
    public String provider;
    public String time;

    public LBSLocation() {
    }

    public LBSLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        provider = location.getProvider();
        time = Utils.formatDateTime(new Date(location.getTime()));
    }
}
