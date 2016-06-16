package com.humorcomciencia.hcc;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by rdasilvar on 16/06/2016.
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent i = new Intent(this, GCMRegistrationIntentService.class);
        startActivity(i);
    }
}
