package at.ac.brgenns.android.mutePhoneInClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = WifiBroadcastReceiver.class.getSimpleName();

    public WifiBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent: " + intent.toString());
        Log.d(TAG, "Action: " + intent.getAction());
        Log.d(TAG, "Extras: " + getExtrasString(intent));
        Intent mutePhoneService = new Intent(context, MutePhoneService.class);
        if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.WIFI_RESULT);
        } else {
            mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.WIFI_STATE_CHANGE);
        }
        context.startService(mutePhoneService);
    }

    private String getExtrasString(Intent intent) {
        String extrasString = "";
        Bundle extras = intent.getExtras();
        try {
            if (extras != null) {
                Set<String> keySet = extras.keySet();
                for (String key : keySet) {
                    try {
                        String extraValue = intent.getExtras().get(key).toString();
                        extrasString += key + ": " + extraValue + "\n";
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
        return extrasString;
    }
}
