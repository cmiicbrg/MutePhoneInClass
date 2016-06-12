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
    private final int DEFAULT_ALARM_INTERVAL = 3;

    public static final String TASK = "Task";
    public static final int FIRST_RUN = 0;
    public static final int BOOT = 1;
    public static final int WIFI_RESULT = 2;
    public static final int ALARM = 3;
    public static final int WIFI_STATE_CHANGE = 4;

    private AudioManager audioManager;
    private AlarmManager alarmManager;
    private PendingIntent pendingNextScan;

    private WifiManager wifiManager;
    private NetworkInfo activeInfo;
    private SharedPreferences prefs;
    private Set<String> prefIDs;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "in onStartCommand");

        reloadPreferences();

        // TODO show the Silencernotification only if the phone was muted and the user might not be aware
        SilencerNotification.notify(this, "Phone muted " + DateFormat
                .getDateTimeInstance().format(new Date()), 1);
        // Set default Task -- Should we use the flag instead?
        int task = ALARM;
        if (intent != null) {
            task = intent.getIntExtra(TASK, ALARM);
        }
        Log.d(TAG, "task was: " + task);
        switch (task) {
            case FIRST_RUN:
                // Schedule Alarm
                setAlarm(DEFAULT_ALARM_INTERVAL);
                // Mute Phone since this has been triggered by choosing the SSID -> the user expects that the phone will now be muted
                // Since at the current state there are no additional settings we use the default: Alarms_only
                setSoundProfile(0);
                Toast.makeText(getApplicationContext(), "Phone muted", Toast.LENGTH_LONG).show();
                break;
            case ALARM:
            case BOOT:
                // Check if connected to one of the WIFIs used for muting
                // if connected check if state of MUTING is ok
                if (!muteBasedOnConnectionInfo()) {
                    // else the WIFI used for muting might not be the one the phone is currently connected to
                    // Request Scan
                    Log.d(TAG, "Requesting Wifi Scan");
                    wifiManager.startScan();
                }
                // Schedule new Alarm (Really? why not only in WIFI_Result -> Because we don't request a scan always!)
                setAlarm(3);
                break;
            case WIFI_RESULT:
                // We received a WIFI RESULT - maybe because another app requested a scan
                // in each case we can reschedule the alarm
                cancelAlarm();
                setAlarm(3);
                // MUTE or UNMUTE Phone
                if (!muteBasedOnScanResult()) {
                    unMute();
                }
                break;
            case WIFI_STATE_CHANGE:
                // if disconected we should probably set a timeout
                if (muteBasedOnConnectionInfo()) {
                    cancelAlarm(); // Is it necessary to cancel the Alarm? Docs say it will remove Alarm from Schedule if there is already a pending Alarm...
                    setAlarm(3);
                } // TODO: else if state is disconnecting
                // set alarm in 1 Minute and set a flag
                break;
            default:
                setAlarm(3);

        }
        return START_STICKY;
    }

    private void unMute() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //TODO: if all was silent ...
    }

    private boolean muteBasedOnScanResult() {
        boolean mute = false;
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Set<String> SSIDs = scanResultToUniqueSSIDStringSet(scanResults);

        // first found Rule takes precedence -> should we have a way to sort rules?
        // or better -> Rules based on Scheduled Classes take precedence! -> TODO
        for (String id : prefIDs) {
            if (prefs.contains("ssid_" + id) && prefs.getBoolean("enable_" + id, true) &&
                    !mute) {
                if (SSIDs.contains(prefs.getString("ssid_" + id, ""))) {
                    setSoundProfile(prefs.getInt("soundProfile_" + id, 0));
                    mute = true;
                }
            }
        }
        return mute;
    }

    /**
     * @return true if already muted correctly or has been muted based on current connection else false
     */
    private boolean muteBasedOnConnectionInfo() {
        boolean mute = false;
        if (isConnectedToWifi()) {
            String currentSSID = wifiManager.getConnectionInfo().getSSID();

            // first found Rule takes precedence -> should we have a way to sort rules?
            // or better -> Rules based on Scheduled Classes take precedence! -> TODO
            for (String id : prefIDs) {
                if (prefs.contains("ssid_" + id) && prefs.getBoolean("enable_" + id, true) &&
                        !mute) {
                    if (prefs.getString("ssid_" + id, "").equals(currentSSID)) {
                        setSoundProfile(prefs.getInt("soundProfile_" + id, 0));
                        mute = true;
                    }
                }
            }
        }
        return mute;
    }

    private void reloadPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefIDs = prefs.getStringSet(MuteSettingsActivity.RULES_KEY, new HashSet<String>());
    }

    private void setSoundProfile(int id) {
        // always set the SoundProfile or check otherwise because the user might have changed the sound options
        if (id == 0) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if (id == 1) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            // TODO: ...
        } else if (id > 1) {
            // load soundProfile from preferences -> not implemented yet
//            SharedPreferences prefs =
//                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            Set<String> IDs =
//                    prefs.getStringSet(MuteSettingsActivity.RULES_KEY, new HashSet<String>());
        }
    }

    private void cancelAlarm() {
        alarmManager.cancel(pendingNextScan);
    }

    private void setAlarm(int inMinutes) {
        Intent nextScan = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingNextScan =
                PendingIntent.getBroadcast(getApplicationContext(), 0, nextScan, 0);
        alarmManager
                .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + inMinutes * 60 * 1000,
                        pendingNextScan);
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

    public static Set<String> scanResultToUniqueSSIDStringSet(List<ScanResult> scanResults) {
        Set<String> scanResultSet = new HashSet<>();
        for (ScanResult scanResult : scanResults) {
            scanResultSet.add(scanResult.SSID);
        }
        return scanResultSet;
    }
}
