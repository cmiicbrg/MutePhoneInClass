package at.ac.brgenns.android.mutePhoneInClass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.ac.brgenns.android.mutePhoneInClass.prefs.MuteSettingsActivity;

/**
 * Created by android on 07.04.2016.
 */
public class MutePhoneService extends Service {
    private static final String TAG = MutePhoneService.class.getSimpleName();

    private long lastScanTime;

    public static final String TASK = "Task";
    public static final int FIRST_RUN = 0;
    public static final int BOOT = 1;
    public static final int WIFI_RESULT = 2;
    public static final int ALARM = 3;
    public static final int WIFI_STATE_CHANGE = 4;

    //    public static final String SSID_PREFERENCES = "SSIDprefs";
//    String chosenSSID;
    private AudioManager audioManager;
    private AlarmManager alarmManager;
    private PendingIntent pendingNextScan;

    String wifisFoundArray[];
    List<ScanResult> wifisFoundList;
    private WifiManager wifiManager;
    private NetworkInfo activeInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"in onStartCommand");
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> IDs = prefs.getStringSet(MuteSettingsActivity.RULES_KEY, new HashSet<String>());

        // TODO show the Silencernotification only if the phone was muted and the user might not be aware
        SilencerNotification.notify(this, "Phone muted " + DateFormat
                .getDateTimeInstance().format(new Date()), 1);

        int task = intent.getIntExtra(TASK, ALARM);
        Log.d(TAG, "task was: " + task);
        switch (task) {
            case FIRST_RUN:
                // Schedule Alarm and mute Phone
                setAlarm(3);

                // Mute Phone since this has been triggered by choosing the SSID -> the user expects that the phone will now be muted
                // Since at the current state there are no additional settings we use the default: Alarms_only
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(getApplicationContext(), "Phone muted", Toast.LENGTH_LONG).show();
                break;
            case ALARM:
            case BOOT:
                // Check if connected to one of the WIFIs used for muting
                // if connected check if state of MUTING is ok
                if (!muteBasedOnConnectionInfo()) {
                    // else (The WIFI used for muting might not be the one the phone is currently connected to
                    // Request Scan

                }
                setAlarm(3);
                // Schedule new Alarm (Really? why not only in WIFI_Result -> Because we don't request a scan always!)
                break;
            case WIFI_RESULT:
                // We received a WIFI RESULT - maybe because another app requested a scan
                // in each case we can reschedule the alarm
                // cancel old alarm
                // schedule new alarm

                // MUTE or UNMUTE Phone

                break;
            case WIFI_STATE_CHANGE:
                // if disconected we should probably set a timeout
                if (muteBasedOnConnectionInfo()) {
                    cancelAlarm();
                    setAlarm(3);
                } //else if state is disconecting
                // set alarm in 1 Minute and set a flag

        }

//        SharedPreferences SSIDs = getSharedPreferences(SSID_PREFERENCES, MODE_PRIVATE);
//        chosenSSID = SSIDs.getString("storedSSID", "iwos");
//        setSilentOrNormal();
        return START_STICKY;
    }

    private void cancelAlarm() {
        alarmManager.cancel(pendingNextScan);
    }

    private void setAlarm(int inMinutes) {
        lastScanTime = System.currentTimeMillis();
        Intent nextScan = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingNextScan =
                PendingIntent.getBroadcast(getApplicationContext(), 0, nextScan, 0);
        alarmManager
                .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + inMinutes * 60 * 1000,
                        pendingNextScan);
    }

    /**
     * @return true if already muted correctly or has been muted based on current connection else false
     */
    private boolean muteBasedOnConnectionInfo() {
        if (isConnectedToWifi()) {
            String currentSSID = wifiManager.getConnectionInfo().getSSID();
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Set<String> IDs =
                    prefs.getStringSet(MuteSettingsActivity.RULES_KEY, new HashSet<String>());

        }

        return false;
    }

    private boolean isConnectedToWifi() {
        boolean ret = false;
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeInfo = connMgr.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnectedOrConnecting()) {
            ret = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }

        return ret;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void setSilentOrNormal() {
//        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        wifi.startScan();
//        wifisFoundList = wifi.getScanResults();
//        wifisFoundArray = new String[wifisFoundList.size()];
//        for (int i = 0; i < wifisFoundList.size(); i++) {
//            wifisFoundArray[i] = wifisFoundList.get(i).SSID;
//        }
//        int i = 0;
//        boolean found = false;
//        while (i < wifisFoundList.size() && !found) {
//            if (wifisFoundArray[i].equals(chosenSSID)) {
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                found = true;
//            } else {
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            }
//            i++;
//        }
//    }
}
