package com.unterhaus.wolfflo.mute_alpha;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;


import java.util.List;

/**
 * Created by wolfflo on 07.04.2016.
 */
public class Wifi_Service extends Service {

    public static final String SSID_PREFERENCES = "SSIDprefs";
    String chosenSSID;
    AudioManager volume_state;


    String wifisFoundArray [];
    List<ScanResult> wifisFoundList;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences SSIDs = getSharedPreferences(SSID_PREFERENCES,MODE_PRIVATE);
        volume_state = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        chosenSSID = SSIDs.getString("storedSSID","iwos");
        setSilentOrNormal();





        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

            return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // do clean up close threads reicevers
    }









    public void setSilentOrNormal() {
        WifiManager wifi =  (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();
        wifisFoundList = wifi.getScanResults();

        wifisFoundArray  = new String[wifisFoundList.size()];
        for(int i = 0; i < wifisFoundList.size(); i++){
            wifisFoundArray[i] = wifisFoundList.get(i).SSID;

        }
        int i = 0;
        boolean found = false;
        while (i < wifisFoundList.size() && !found) {
            if (wifisFoundArray[i].equals(chosenSSID)){
                volume_state.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                found = true;
            } else {

                volume_state.setRingerMode(AudioManager.RINGER_MODE_NORMAL);


            }
            i++;
        }
    }


}
