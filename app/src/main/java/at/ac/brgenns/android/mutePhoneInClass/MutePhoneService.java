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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import at.ac.brgenns.android.mutePhoneInClass.prefs.PreferenceHelper;
import at.ac.brgenns.android.mutePhoneInClass.prefs.SettingKeys;

//import java.util.Date;

//import at.dAuzinger.kusssApi.DateTime;

/**
 * Created by android on 07.04.2016.
 */
public class MutePhoneService extends Service {
    public static final String TASK = "Task";
    public static final int WIFI_RULE_ADDED = 0;
    public static final int BOOT = 1;
    public static final int WIFI_RESULT = 2;
    public static final int ALARM = 3;
    public static final int WIFI_STATE_CHANGE = 4;
    public static final int ENABLE = 6;
    public static final int KUSS_ACCOUNT = 7;
    public static final int APP_START = 8;
    private static final String TAG = MutePhoneService.class.getSimpleName();
    //TODO! Alarm Interval
    private final int DEFAULT_ALARM_INTERVAL = 1; //in Minutes
    private AudioManager audioManager;
    private AlarmManager alarmManager;
    private PendingIntent pendingNextScan;

    private WifiManager wifiManager;
    private NetworkInfo activeInfo;
    private SharedPreferences prefs;
    private Set<String> prefIDs;
    private String reason = "unknown";

    public static Set<String> scanResultToUniqueSSIDStringSet(List<ScanResult> scanResults) {
        Set<String> scanResultSet = new TreeSet<>();
        for (ScanResult scanResult : scanResults) {
            if (!scanResult.SSID.isEmpty()) {
                scanResultSet.add(scanResult.SSID);
            }
        }
        return scanResultSet;
    }

    public static Set<String> configuredNetworksToUniqueSSIDStringSet(
            List<WifiConfiguration> configuredNetworks) {
        Set<String> configuredNetworksSet = new TreeSet<>();
        for (WifiConfiguration config : configuredNetworks) {
            configuredNetworksSet.add(config.SSID.replace("\"", ""));
        }
        return configuredNetworksSet;
    }

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

        if (hasRules() && prefs.getBoolean(SettingKeys.MUTE_ENABLED, true) ||
                task == ENABLE) {

            switch (task) {
                case WIFI_RULE_ADDED:
                    // Enable the BootCompletedReceiver and WifiBroadcastReceiver and
                    enableReceivers();
                    // Schedule Alarm
                    setAlarm(DEFAULT_ALARM_INTERVAL, ALARM);
                    //It's possible that the chosen WIfi is already nearby or connected
                    if (!muteBasedOnConnectionInfo()) {
                        Log.d(TAG, "Requesting Wifi Scan");
                        wifiManager.startScan();
                    }
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
                    setAlarm(DEFAULT_ALARM_INTERVAL, ALARM);
                    break;
                case WIFI_RESULT:
                    // We received a WIFI RESULT - maybe because another app or the system requested a scan
                    // in each case we can reschedule the alarm
                    cancelAlarm();
                    setAlarm(DEFAULT_ALARM_INTERVAL, ALARM);
                    // MUTE or UNMUTE Phone
                    if (!muteBasedOnScanResult()) {
                        unMute();
                    }
                    break;
                case WIFI_STATE_CHANGE:
                    // if disconected we should probably set a timeout
                    if (muteBasedOnConnectionInfo()) {
                        cancelAlarm(); // Is it necessary to cancel the Alarm? Docs say it will remove Alarm from Schedule if there is already a pending Alarm...
                        setAlarm(1, ALARM);
                    } // TODO: else if state is disconnecting
                    // set alarm in 1 Minute and set a flag
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
                    setAlarm(DEFAULT_ALARM_INTERVAL, ALARM);
                    break;
                case KUSS_ACCOUNT:
                    (new KusssScheduleSync(this)).execute(new String[0]);
                default:
                    setAlarm(DEFAULT_ALARM_INTERVAL, ENABLE);
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

    private boolean hasRules() {
        boolean hasRules = false;
        Iterator<String> it = prefIDs.iterator();
        while (it.hasNext() && !hasRules) {
            String id = it.next();
            hasRules = prefs.contains(SettingKeys.Wifi.SSID + "_" + id) ||
                    prefs.contains(SettingKeys.Kusss.USER + "_" + id) ||
                    prefs.contains(SettingKeys.ICS.ICS_URL + "_" + id) ||
                    prefs.contains(SettingKeys.WebUntis.SERVER_URL + "_" + id);
        }
        return hasRules;
    }

    private boolean muteBasedOnScanResult() {
        boolean mute = false;
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Set<String> SSIDs = scanResultToUniqueSSIDStringSet(scanResults);
        Log.d(TAG, SSIDs.toString());
        // first found Rule takes precedence -> should we have a way to sort rules?
        // or better -> Rules based on Scheduled Classes take precedence! -> TODO
        for (String id : prefIDs) {
            if (PreferenceHelper.getRuleType(prefs,id) == SettingKeys.SettingType.WIFI) {
                String cKey = SettingKeys.Wifi.SSID + "_" + id;
                Log.d(TAG, cKey);
                if (prefs.getBoolean(SettingKeys.Wifi.ENABLE + "_" + id, true) &&
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
                if (PreferenceHelper.getRuleType(prefs,id) == SettingKeys.SettingType.WIFI &&
                        prefs.getBoolean(SettingKeys.Wifi.ENABLE + "_" + id, true) &&
                        !mute) {
                    if (prefs.getString(SettingKeys.Wifi.SSID + "_" + id, "")
                            .equals(currentSSID)) {
                        reason = currentSSID;
                        setSoundProfile(
                                prefs.getString(SettingKeys.Wifi.SOUND_PROFILE + "_" + id,
                                        "0"));
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
        //only unMute if we have LastAudioSettings otherwise we would constantly change the volume
        // otherwise do nothing since the settings have already been restored and deleted!
        if (prefs.contains(SettingKeys.LAST_RINGER_MODE) &&
                prefs.contains(SettingKeys.LAST_ALARM_VOLUME) &&
                prefs.contains(SettingKeys.LAST_MEDIA_VOLUME) &&
                prefs.contains(SettingKeys.LAST_RINGER_VOLUME)) {
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
            SilencerNotification.cancel(this);
        }
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
            int mediaVolume =
                    prefs.getInt(SettingKeys.SoundProfile.MEDIA_VOLUME + "_" + id, -1);
            int alarmVolume =
                    prefs.getInt(SettingKeys.SoundProfile.ALARM_VOLUME + "_" + id, -1);
            int ringVolume =
                    prefs.getInt(SettingKeys.SoundProfile.RINGER_VOLUME + "_" + id, -1);
            boolean vibrate =
                    prefs.getBoolean(SettingKeys.SoundProfile.VIBRATE + "_" + id, false);
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

    private boolean shouldAdjustAudioSettings(int alarmVolume, int mediaVolume,
                                              int ringerVolume,
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
        Log.d(TAG, "Setting alarm in " + inMinutes);
        Intent nextScan = new Intent(getApplicationContext(), AlarmReceiver.class);
        nextScan.putExtra(TASK, intentExtra);
        pendingNextScan =
                PendingIntent.getBroadcast(getApplicationContext(), 0, nextScan,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager
                .set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + inMinutes * 60 * 1000,
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
