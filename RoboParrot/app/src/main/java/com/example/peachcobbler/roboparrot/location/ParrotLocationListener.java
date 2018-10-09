package com.example.peachcobbler.roboparrot.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class ParrotLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOCATION UPDATE: ", location.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
