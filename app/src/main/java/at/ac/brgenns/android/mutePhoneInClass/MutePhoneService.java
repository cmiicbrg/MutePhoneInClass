package at.ac.brgenns.android.mutePhoneInClass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import at.ac.brgenns.android.mutePhoneInClass.prefs.SettingKeys;

/**
 * Created by android on 07.04.2016.
 */
public class MutePhoneService extends Service {
    private static final String TAG = MutePhoneService.class.getSimpleName();
    private final int DEFAULT_ALARM_INTERVAL = 3; //in Minutes

    public static final String TASK = "Task";

    public static final int WIFI_RULE_ADDED = 0;
    public static final int BOOT = 1;
    public static final int WIFI_RESULT = 2;
    public static final int ALARM = 3;
    public static final int WIFI_STATE_CHANGE = 4;
    public static final int DISABLE = 5;
    public static final int ENABLE = 6;

    private AudioManager audioManager;
    private AlarmManager alarmManager;
    private PendingIntent pendingNextScan;

    private WifiManager wifiManager;
    private NetworkInfo activeInfo;
    private SharedPreferences prefs;
    private Set<String> prefIDs;
    private String reason = "unknown";

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

        // only do any work if there are rules...
        // currently the WIFI_RULE_ADDED Flag is set on every addition of a rule. This will also enable the receivers if needed.
        // Set default Task -- Should we use the flag instead?
        int task = ALARM;
        if (intent != null) {
            task = intent.getIntExtra(TASK, ALARM);
        }
        Log.d(TAG, "task was: " + task);

        if (hasWifiRules() && prefs.getBoolean(SettingKeys.MUTE_ENABLED, true) ||
                task == ENABLE) {

            switch (task) {
                case WIFI_RULE_ADDED:
                    // Enable the BootCompletedReceiver and WifiBroadcastReceiver and
                    enableReceivers();
                    // Schedule Alarm
                    setAlarm(DEFAULT_ALARM_INTERVAL, ALARM);
                    // Mute Phone since this has been triggered by choosing the SSID -> the user expects that the phone will now be muted
                    // Since at the current state there are no additional settings we use the default: Alarms_only
                    setSoundProfile("0");
                    Toast.makeText(getApplicationContext(), "Phone muted", Toast.LENGTH_LONG)
                            .show();
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
                    setAlarm(3, ALARM);
                    break;
                case WIFI_RESULT:
                    // We received a WIFI RESULT - maybe because another app or the system requested a scan
                    // in each case we can reschedule the alarm
                    cancelAlarm();
                    setAlarm(3, ALARM);
                    // MUTE or UNMUTE Phone
                    if (!muteBasedOnScanResult()) {
                        unMute();
                    }
                    break;
                case WIFI_STATE_CHANGE:
                    // if disconected we should probably set a timeout
                    if (muteBasedOnConnectionInfo()) {
                        cancelAlarm(); // Is it necessary to cancel the Alarm? Docs say it will remove Alarm from Schedule if there is already a pending Alarm...
                        setAlarm(3, ALARM);
                    } // TODO: else if state is disconnecting
                    // set alarm in 1 Minute and set a flag
                    break;
                case DISABLE:
                    unMute();
                    cancelAlarm();
                    SilencerNotification.cancel(this);
                    disableReceivers();
                    break;
                case ENABLE:
                    if (!prefs.getBoolean(SettingKeys.MUTE_ENABLED, true)) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(SettingKeys.MUTE_ENABLED, true);
                        editor.commit();
                    }
                    cancelAlarm();
                    enableReceivers();
                    if (!muteBasedOnConnectionInfo()) {
                        Log.d(TAG, "Requesting Wifi Scan");
                        wifiManager.startScan();
                    }
                    setAlarm(3, ALARM);
                    break;
                default:
                    setAlarm(3, ENABLE);
            }
        } else {
            // Make sure we won't be doing any work if there are no rules...
            unMute();
            cancelAlarm();
            SilencerNotification.cancel(this);
            disableReceivers();
            int disabledfor = prefs.getInt(SettingKeys.DISABLED_FOR, 0);
            if (disabledfor > 0) {
                setAlarm(disabledfor, ENABLE);
            }
        }
        return START_STICKY;
    }

    private boolean hasWifiRules() {
        for (String id : prefIDs) {
            if (prefs.contains(SettingKeys.Wifi.SSID + "_" + id)) {
                return true;
            }
        }
        return false;
    }

    private boolean muteBasedOnScanResult() {
        boolean mute = false;
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Set<String> SSIDs = scanResultToUniqueSSIDStringSet(scanResults);
        Log.d(TAG, SSIDs.toString());
        // first found Rule takes precedence -> should we have a way to sort rules?
        // or better -> Rules based on Scheduled Classes take precedence! -> TODO
        for (String id : prefIDs) {
            String cKey = SettingKeys.Wifi.SSID + "_" + id;
            Log.d(TAG, cKey);
            if (prefs.contains(cKey) &&
                    prefs.getBoolean(SettingKeys.Wifi.ENABLE + "_" + id, true) &&
                    !mute) {
                if (SSIDs.contains(prefs.getString(cKey, ""))) {
                    String cSoundProfile =
                            prefs.getString(SettingKeys.Wifi.SOUND_PROFILE + "_" + id, "0");
                    Log.d(TAG, cSoundProfile);
                    reason = prefs.getString(cKey, "");
                    setSoundProfile(cSoundProfile);
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
                if (prefs.contains(SettingKeys.Wifi.SSID + id) &&
                        prefs.getBoolean(SettingKeys.Wifi.ENABLE + "_" + id, true) &&
                        !mute) {
                    if (prefs.getString(SettingKeys.Wifi.SSID + "_" + id, "").equals(currentSSID)) {
                        reason = currentSSID;
                        setSoundProfile(
                                prefs.getString(SettingKeys.Wifi.SOUND_PROFILE + "_" + id, "0"));
                        mute = true;
                    }
                }
            }
        }
        return mute;
    }

    private void reloadPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefIDs = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
    }

    private void unMute() {
        audioManager.setRingerMode(
                prefs.getInt(SettingKeys.LAST_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL));
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                prefs.getInt(SettingKeys.LAST_ALARM_VOLUME,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) / 2), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                prefs.getInt(SettingKeys.LAST_MEDIA_VOLUME,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_RING,
                prefs.getInt(SettingKeys.LAST_RINGER_VOLUME,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) / 2), 0);
        deleteLastAudioSettings();

    }

    private void setSoundProfile(String id) {
        // always set the SoundProfile or check otherwise because the user might have changed the sound options
        if (id.equals("0") &&
                shouldAdjustAudioSettings(-1, 0, 0, AudioManager.RINGER_MODE_SILENT)) {
            saveLastAudioSettings();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        } else if (id.equals("1") &&
                shouldAdjustAudioSettings(0, 0, 0, AudioManager.RINGER_MODE_SILENT)) {
            saveLastAudioSettings();
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else { // load SoundProfile from Preferences
            int mediaVolume = prefs.getInt(SettingKeys.SoundProfile.MEDIA_VOLUME + "_" + id, -1);
            int alarmVolume = prefs.getInt(SettingKeys.SoundProfile.ALARM_VOLUME + "_" + id, -1);
            int ringVolume = prefs.getInt(SettingKeys.SoundProfile.RINGER_VOLUME + "_" + id, -1);
            boolean vibrate = prefs.getBoolean(SettingKeys.SoundProfile.VIBRATE + "_" + id, false);
            int ringerMode = AudioManager.RINGER_MODE_NORMAL;
            if (vibrate && ringVolume >= 0) {
                ringerMode = AudioManager.RINGER_MODE_VIBRATE;
            } else if (!vibrate && ringVolume == 0) {
                ringerMode = AudioManager.RINGER_MODE_SILENT;
            }
            if (shouldAdjustAudioSettings(alarmVolume, mediaVolume, ringVolume, ringerMode)) {
                saveLastAudioSettings();
                if (vibrate && ringVolume >= 0) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, 0);
                } else if (!vibrate && ringVolume == 0) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else if (!vibrate && ringVolume > 0) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, 0);
                }
                if (alarmVolume >= 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0);
                }
                if (mediaVolume >= 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, mediaVolume, 0);
                }
            }
        }
    }

    private boolean shouldAdjustAudioSettings(int alarmVolume, int mediaVolume, int ringerVolume,
                                              int ringerMode) {
        boolean settingsOK = true;
        settingsOK &= audioManager.getRingerMode() == ringerMode;
        if (alarmVolume >= 0 && settingsOK) {
            settingsOK = audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == alarmVolume;
        }
        if (mediaVolume >= 0 && settingsOK) {
            settingsOK = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == mediaVolume;
        }
        if (ringerVolume >= 0 && settingsOK) {
            settingsOK = audioManager.getStreamVolume(AudioManager.STREAM_RING) == ringerVolume;
        }

        return !settingsOK;
    }

    private void saveLastAudioSettings() {

        if (!prefs.contains(SettingKeys.LAST_RINGER_MODE)) {
            SilencerNotification.notify(this, reason, 1);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SettingKeys.LAST_ALARM_VOLUME,
                    audioManager.getStreamVolume(AudioManager.STREAM_ALARM))
                    .putInt(SettingKeys.LAST_MEDIA_VOLUME,
                            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
                    .putInt(SettingKeys.LAST_RINGER_VOLUME,
                            audioManager.getStreamVolume(AudioManager.STREAM_RING))
                    .putInt(SettingKeys.LAST_RINGER_MODE, audioManager.getRingerMode());
            editor.apply();
        }
    }

    private void deleteLastAudioSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(SettingKeys.LAST_ALARM_VOLUME)
                .remove(SettingKeys.LAST_MEDIA_VOLUME)
                .remove(SettingKeys.LAST_RINGER_VOLUME)
                .remove(SettingKeys.LAST_RINGER_MODE);
        editor.apply();
    }

    private void cancelAlarm() {
        alarmManager.cancel(pendingNextScan);
    }

    private void setAlarm(int inMinutes, int intentExtra) {
        Intent nextScan = new Intent(getApplicationContext(), AlarmReceiver.class);
        nextScan.putExtra(TASK, intentExtra);
        pendingNextScan =
                PendingIntent.getBroadcast(getApplicationContext(), 0, nextScan,
                        PendingIntent.FLAG_UPDATE_CURRENT);
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

    private void enableReceivers() {
        ComponentName receiver = new ComponentName(this, BootCompletedReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        receiver = new ComponentName(this, WifiBroadcastReceiver.class);
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableReceivers() {
        // not disabling boot complete receiver (problem if disabled for... and device is rebooted)
        ComponentName receiver = new ComponentName(this, WifiBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
