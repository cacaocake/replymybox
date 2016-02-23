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

import com.ebaykorea.escrow.replymybox.R;
import com.ebaykorea.escrow.replymybox.model.BoxModel;
import com.ebaykorea.escrow.replymybox.model.BoxRepository;
import com.ebaykorea.escrow.replymybox.model.LocationModel;
import com.ebaykorea.escrow.replymybox.model.LocationRepository;
import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationService extends Service implements LocationListener{

    private RestAdapter adapter;

    private final String LOG_TAG = LocationService.class.getSimpleName();
    private static final int MIN_TIME_INTERVAL = 1000 * 60 * 2; // milliseconds
    private static final int MIN_DISTANCE_CHANGE = 10; // meters

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setLocationListener();

        return START_STICKY;
    }

    private void setLocationListener() {
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_INTERVAL, MIN_DISTANCE_CHANGE, this);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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

        RestAdapter adapter = getLoopBackAdapter();

        LocationRepository repository = adapter.createRepository(LocationRepository.class);
        LocationModel model = repository.createObject(ImmutableMap.of("memberid", "cockroach419"));
        model.setLatitude(Double.toString(location.getLatitude()));
        model.setLongitude(Double.toString(location.getLongitude()));

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        model.setDate(dateFormat.format(date));

        model.save(new VoidCallback() {
            @Override
            public void onSuccess() {
                Log.d("location.save", "success");
            }

            @Override
            public void onError(Throwable t) {
                Log.d("location.save", t.getMessage());
                Log.d("location.save", t.getLocalizedMessage());
                StackTraceElement[] ste = t.getStackTrace();
                for (StackTraceElement s : ste) {
                    Log.d("location.save", s.toString());
                }
            }
        });

    }

    private RestAdapter getLoopBackAdapter() {
        if (adapter == null) {

            adapter = new RestAdapter(
                    getApplicationContext(), getString(R.string.rest_end_point));

        }
        return adapter;
    }
}
