package com.ebaykorea.escrow.replymybox.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by cacao on 2016. 3. 2..
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    private static final String TAG = MyInstanceIDListenerService.class.getName();

    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegistrationService.class);
        startService(intent);
    }
}
