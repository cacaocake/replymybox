package com.ebaykorea.escrow.replymybox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.ebaykorea.escrow.replymybox.model.BoxModel;
import com.ebaykorea.escrow.replymybox.model.DeliveryModel;
import com.ebaykorea.escrow.replymybox.model.LocationModel;
import com.ebaykorea.escrow.replymybox.rest.BoxInterface;
import com.ebaykorea.escrow.replymybox.rest.DeliveryInterface;
import com.ebaykorea.escrow.replymybox.rest.LocationInterface;
import com.ebaykorea.escrow.replymybox.rest.RestServiceGenerator;
import com.ebaykorea.escrow.replymybox.service.GcmRegistrationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strongloop.android.loopback.RestAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cacao on 2016. 2. 24..
 */
public class ClientActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private RestAdapter adapter;
    private String memberid;
    static final LatLng SEOUL = new LatLng( 37.56, 126.97);

    private static final String TAG = ClientActivity.class.getName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getDelivery();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 10000);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.client_map);
        mapFragment.getMapAsync(this);

        registBroadcastReceiver();
        getInstanceIdToken();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng mapCenter = SEOUL;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));
    }

    public void getBox(String boxid) {
        if(TextUtils.isEmpty(boxid)){
            Log.d("getBox", "boxid가 빈값이네요 ");
            return;
        }
        BoxInterface boxInterface = RestServiceGenerator.createService(BoxInterface.class);

        HashMap<String, String> queryMap = new HashMap<String,String>();

        queryMap.put("filter[where][boxid]", boxid);
        queryMap.put("filter[order]", "id DESC");
        queryMap.put("filter[limit]", "1");

        Call<List<BoxModel>> call = boxInterface.listRepos(queryMap);
        call.enqueue(new Callback<List<BoxModel>>() {
            @Override
            public void onResponse(Call<List<BoxModel>> call, Response<List<BoxModel>> response) {
                if (response.isSuccess()) {
                    List<BoxModel> boxList = response.body();
                    if (boxList.size() > 0) {
                        BoxModel model = boxList.get(0);
                        getRecentLocations(model.getMemberid());
                        memberid = model.getMemberid();
                    } else {
                        Log.d("getBox", "자료가 없네영...");
                    }
                } else {
                    Log.d("getBox", "자료가 없네영...");
                }
            }

            @Override
            public void onFailure(Call<List<BoxModel>> call, Throwable t) {
                Log.e("getBox", t.getMessage());
            }
        });
    }

    public void getDelivery() {
        DeliveryInterface deliveryInterface = RestServiceGenerator.createService(DeliveryInterface.class);

        HashMap<String, String> queryMap = new HashMap<String,String>();

        queryMap.put("filter[where][buyerid]", "roach419");
        queryMap.put("filter[order]", "id DESC");
        queryMap.put("filter[limit]", "1");

        Call<List<DeliveryModel>> call = deliveryInterface.listRepos(queryMap);
        call.enqueue(new Callback<List<DeliveryModel>>() {
            @Override
            public void onResponse(Call<List<DeliveryModel>> call, Response<List<DeliveryModel>> response) {
                if (response.isSuccess()) {

                    List<DeliveryModel> deliveryList = response.body();
                    if (deliveryList.size() > 0) {
                        DeliveryModel model = deliveryList.get(0);
                        getBox(model.getBoxid());
                        Log.d("getDelivery", "boxid : " + model.getBoxid());
                    } else {
                        Log.d("getDelivery", "자료가 없네영...");
                    }

                } else {
                    Log.d("getDelivery", "자료가 없네영...");
                }
            }

            @Override
            public void onFailure(Call<List<DeliveryModel>> call, Throwable t) {
                Log.e("getDelivery", t.getMessage());
            }
        });
    }

    public void getRecentLocations(String memberid){
        if(TextUtils.isEmpty(memberid)){
            Log.d("getRecentLocations", "memberid가 빈값이네요 ");
            return;
        } else if(googleMap == null) {
            Log.d("getRecentLocations", "googleMap이 준비되지 않았어요");
            return;
        }

        LocationInterface locationInterface = RestServiceGenerator.createService(LocationInterface.class);

        HashMap<String, String> queryMap = new HashMap<String,String>();
        queryMap.put("filter[where][memberid]", memberid);
        queryMap.put("filter[order]", "id DESC");
        queryMap.put("filter[limit]", "20");

        Call<List<LocationModel>> call = locationInterface.listRepos(queryMap);
        call.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(Call<List<LocationModel>> call, Response<List<LocationModel>> response) {
                if (response.isSuccess()) {
                    List<LocationModel> locationList = response.body();
                    if (locationList.size() > 0) {
                        googleMap.clear();
                        for (int i = 0; i < locationList.size(); i++) {
                            LocationModel location = locationList.get(i);
                            LatLng lo = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()));

                            googleMap.addMarker(new MarkerOptions()
                                            .title(location.getMemberid())
                                            .snippet(location.getDate().replace('T', ' '))
                                            .icon(BitmapDescriptorFactory.defaultMarker(i * 360 / locationList.size()))
                                            .position(lo)
                            );
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lo, 13));
                        }
                    } else {
                        Log.d("getRecentLocations", "자료가 없네영...");
                    }

                } else {
                    Log.d("getRecentLocations", "자료가 없네영...");
                }

            }

            @Override
            public void onFailure(Call<List<LocationModel>> call, Throwable t) {
                Log.e("getRecentLocations", t.getMessage());
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(BroadcastActions.REGISTRATION_READY)){

                } else if(action.equals(BroadcastActions.REGISTRATION_GENERATING)){

                } else if(action.equals(BroadcastActions.REGISTRATION_COMPLETE)){
                    String token = intent.getStringExtra("token");
                    // todo : 토큰으로 뭘 합시당
                    Log.d("token", token);
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(BroadcastActions.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(BroadcastActions.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(BroadcastActions.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, GcmRegistrationService.class);
            startService(intent);
        }
    }

}
