package com.ebaykorea.escrow.replymybox.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.ebaykorea.escrow.replymybox.rest.LocationInterface;

import retrofit2.Retrofit;


public class LocationAlarm extends BroadcastReceiver {
    public LocationAlarm() {
        super();
    }

    private final String LOG_TAG = LocationAlarm.class.getSimpleName();
    private Retrofit retrofit;
    private LocationInterface locationInterface;
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // todo get location
        Location location = getLocation(context);
        if(location == null) {
            Log.i(LOG_TAG, "getLocation() fail");
        } else {
            Log.i(LOG_TAG, "getLocation() " + location.getLatitude());
            Log.i(LOG_TAG, "getLocation() " + location.getLongitude());
        }


        Log.i(LOG_TAG, "오오오옹!!");
        wl.release();
    }

    public void setAlarm(Context context, String baseUrl) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 2, pi);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, LocationAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private Location getLocation(Context context) {
        Location location = null;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (isGPSEnabled && location != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }


        return location;
    }
}
