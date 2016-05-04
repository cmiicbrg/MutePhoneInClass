package com.unterhaus.wolfflo.mute_alpha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wolfflo on 14.04.2016.
 */
public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, Wifi_Service.class);
        context.startService(service);


    }
}
