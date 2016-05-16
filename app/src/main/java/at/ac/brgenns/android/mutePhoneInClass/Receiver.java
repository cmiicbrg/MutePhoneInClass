package at.ac.brgenns.android.mutePhoneInClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by android on 14.04.2016.
 */
public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, Wifi_Service.class);
        context.startService(service);
    }
}
