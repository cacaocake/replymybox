package com.ebaykorea.escrow.replymybox.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener{

    //private LocationAlarm locationAlarm = new LocationAlarm();

    private final String LOG_TAG = LocationService.class.getSimpleName();
    private static final int MIN_TIME_INTERVAL = 1000 * 60 * 2; // milliseconds
    private static final int MIN_DISTANCE_CHANGE = 10; // meters

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setLocationListener();
        //locationAlarm.setAlarm(this);
        return START_STICKY;
    }

    private void setLocationListener() {
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE_CHANGE, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "onLocationChanged() " + location.getLatitude());
        Log.i(LOG_TAG, "onLocationChanged() " + location.getLongitude());

    }
}
