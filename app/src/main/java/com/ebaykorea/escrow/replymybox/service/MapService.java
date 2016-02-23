package com.ebaykorea.escrow.replymybox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ebaykorea.escrow.replymybox.R;
import com.ebaykorea.escrow.replymybox.rest.LocationInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Retrofit;

/**
 * Created by cacao on 2016. 2. 19..
 */
public class MapService extends Service {
    private final String LOG_TAG = MapService.class.getSimpleName();
    private GoogleMap googleMap;
    private Marker locationMarker;
    private Retrofit retrofit;
    private LocationInterface locationInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
