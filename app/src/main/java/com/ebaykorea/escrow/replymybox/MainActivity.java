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

import com.ebaykorea.escrow.replymybox.service.LocationService;
import com.google.common.collect.ImmutableMap;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

public class MainActivity extends ActionBarActivity {

    private RestAdapter adapter;

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

//                ModelRepository boxRepository = adapter.createRepository("box");
//                Model box = boxRepository.createObject( ImmutableMap.of("boxid", re) );
//                box.save(new VoidCallback(){
//                    @Override
//                    public void onSuccess() {
//                        Log.d("box.save", "success");
//                    }
//                    @Override
//                    public void onError(Throwable t) {
//                        Log.d("box.save", t.getMessage());
//                        Log.d("box.save", t.getLocalizedMessage());
//                        StackTraceElement[] ste = t.getStackTrace();
//                        for (StackTraceElement s : ste) {
//                            Log.d("box.save",s.toString());
//                        }
//
//                    }
//                });
            }

        }


    }

    private RestAdapter getLoopBackAdapter() {
        if (adapter == null) {

            adapter = new RestAdapter(
                    getApplicationContext(), "http://ec2-52-79-92-220.ap-northeast-2.compute.amazonaws.com:3000/api");

        }
        return adapter;
    }

    public static class BoxModel extends Model {
        private String boxid;
        private String memberid;
        private String insdate;

        public String getBoxid() {
            return boxid;
        }

        public void setBoxid(String boxid) {
            this.boxid = boxid;
        }

        public String getMemberid() {
            return memberid;
        }

        public void setMemberid(String memberid) {
            this.memberid = memberid;
        }

        public String getInsdate() {
            return insdate;
        }

        public void setInsdate(String insdate) {
            this.insdate = insdate;
        }


    }

    public static class BoxRepository extends ModelRepository<BoxModel> {
        public BoxRepository() {
            super("box", "box", BoxModel.class);
        }
    }
}
