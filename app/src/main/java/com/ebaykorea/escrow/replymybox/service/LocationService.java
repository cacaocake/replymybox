package com.ebaykorea.escrow.replymybox.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by cacao on 2016. 1. 23..
 */
public class LocationService extends Service{

    private LocationAlarm locationAlarm = new LocationAlarm();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationAlarm.setAlarm(this);
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
