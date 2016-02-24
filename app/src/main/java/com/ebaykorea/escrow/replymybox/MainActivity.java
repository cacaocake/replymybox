package com.ebaykorea.escrow.replymybox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ebaykorea.escrow.replymybox.model.BoxModel;
import com.ebaykorea.escrow.replymybox.model.BoxRepository;
import com.ebaykorea.escrow.replymybox.model.LocationModel;
import com.ebaykorea.escrow.replymybox.model.LocationModelRetrofit;
import com.ebaykorea.escrow.replymybox.model.LocationRepository;
import com.ebaykorea.escrow.replymybox.rest.LocationInterface;
import com.ebaykorea.escrow.replymybox.rest.RestServiceGenerator;
import com.ebaykorea.escrow.replymybox.service.LocationService;
import com.ebaykorea.escrow.replymybox.service.MapService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.ImmutableMap;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {

    private RestAdapter adapter;

    static final LatLng SEOUL = new LatLng( 37.56, 126.97);

    private GoogleMap googleMap;
    private Marker locationMarker;

    private LocationInterface locationInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button scan = (Button)findViewById(R.id.barcode_scan_button);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public void scanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            this.startService(serviceIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if(scanResult.getContents() != null) {
                String re = scanResult.getContents();
                Log.d("onActivityResult", re);

                TextView barcodeTextView = (TextView)findViewById(R.id.barcode_textview);
                barcodeTextView.setText(re);

                RestAdapter adapter = getLoopBackAdapter();

                BoxRepository repository = adapter.createRepository(BoxRepository.class);
                BoxModel model = repository.createObject(ImmutableMap.of("boxid", re));
                model.setMemberid("cockroach419"); // todo : 나중에 로그인 추가 되면 구현
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                model.setInsdate(dateFormat.format(date));

                model.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("box.save", "success");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d("box.save", t.getMessage());
                        Log.d("box.save", t.getLocalizedMessage());
                        StackTraceElement[] ste = t.getStackTrace();
                        for (StackTraceElement s : ste) {
                            Log.d("box.save",s.toString());
                        }
                    }
                });

            }

        }


    }

    private RestAdapter getLoopBackAdapter() {
        if (adapter == null) {

            adapter = new RestAdapter(
                    getApplicationContext(), getString(R.string.rest_end_point));

        }
        return adapter;
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        LatLng mapCenter = SEOUL;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

        Marker marker = googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.box48))
                        .position(mapCenter)
                        .flat(true)
                        .anchor(0.0f, 0.0f)
        );

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(14)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);


        locationMarker = marker;

        getRecentLocations();
    }

    public void getRecentLocations(){
        locationInterface = RestServiceGenerator.createService(LocationInterface.class);

        HashMap<String, String> queryMap = new HashMap<String,String>();
        queryMap.put("filter[where][memberid]", "cockroach419");
        queryMap.put("filter[order]", "id DESC");
        queryMap.put("filter[limit]", "10");

        Call<List<LocationModelRetrofit>> call = locationInterface.listRepos(queryMap);
        call.enqueue(new Callback<List<LocationModelRetrofit>>() {
            @Override
            public void onResponse(Call<List<LocationModelRetrofit>> call, Response<List<LocationModelRetrofit>> response) {
                if (response.isSuccess()) {
                    googleMap.clear();
                    List<LocationModelRetrofit> locationList = response.body();
                    //for (LocationModelRetrofit location : locationList) {
                    for (int i = 0; i < locationList.size(); i++) {
                        LocationModelRetrofit location = locationList.get(i);
                        LatLng lo = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()));

                        googleMap.addMarker(new MarkerOptions()
                                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.box48))
                                        .title(location.getMemberid())
                                        .snippet(location.getDate())
                                        .icon(BitmapDescriptorFactory.defaultMarker(i * 360 / locationList.size()))
                                        .position(lo)
                        );
                        Log.d("box.save", "latitude " + lo.latitude);
                        Log.d("box.save", "longitude " + lo.longitude);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lo, 13));

                    }
                } else {
                    Log.d("box.save", "자료가 없네영...");
                }

            }

            @Override
            public void onFailure(Call<List<LocationModelRetrofit>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.e("box.save", t.getMessage());
            }
        });
    }
}
