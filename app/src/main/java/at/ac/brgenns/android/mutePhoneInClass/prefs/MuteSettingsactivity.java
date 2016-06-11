package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import at.ac.brgenns.android.mutePhoneInClass.FirstRunSSIDChooser;
import at.ac.brgenns.android.mutePhoneInClass.R;
import at.ac.brgenns.android.mutePhoneInClass.prefs.db.PreferenceDataSource;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.EventProvider;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.WifiEvent;

public class MuteSettingsActivity extends PreferenceActivity
        implements FirstRunSSIDChooser.SSIDChosenListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String RULES_KEY = "rule_ids";
    private static final int REQUIRED_PERMISSIONS_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final String TAG = MuteSettingsActivity.class.getSimpleName();

    public GoogleApiClient mGoogleApiClient;
    private PreferenceDataSource datasource;
    private TreeMap<Long, WifiEvent> wifiEvents;
    private TreeMap<Long, EventProvider> eventProviders;
    private String[] ssidsFoundArray;
    private Set<String> ids;
    public static final String[] PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE};
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (!(preference instanceof RingtonePreference)) {

                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    public static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasPermissionsToScanWifi()) {
            requestPermissions(PERMISSIONS,
                    REQUIRED_PERMISSIONS_REQUEST_CODE);
        } else if (getIDs().isEmpty()) {
            firstRunScanAndShowWifi();
        }
        setContentView(R.layout.activity_mute_settings);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermissionsToScanWifi() {
        boolean hasPermissions = true;
        for (String permission : PERMISSIONS) {
            hasPermissions &= checkSelfPermission(permission) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return hasPermissions;
    }

    private void firstRunScanAndShowWifi() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();
        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(this);
                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> wifisFoundList = wifi.getScanResults();
//            WifiInfo info = wifi.getConnectionInfo();
                if (!wifisFoundList.isEmpty()) {
                    ssidsFoundArray = new String[wifisFoundList.size()];
                    for (int i = 0; i < wifisFoundList.size(); i++) {
                        ssidsFoundArray[i] = wifisFoundList.get(i).SSID;
                    }
                    FirstRunSSIDChooser firstRunSSIDChooser = new FirstRunSSIDChooser();
                    firstRunSSIDChooser.setOptions(ssidsFoundArray);
                    firstRunSSIDChooser.show(getFragmentManager(), "dosth");
                } else {
                    // There seems to be a bug in 6.0 https://code.google.com/p/android/issues/detail?id=185370
                    // getScanResults returns an empty list when Locationservice is turned off
                    // Google says it's by design but:
                    // this seems not to be an issue on 5.1.1 and 6.0.1 should be tested with 4.4,...
                    // this would run counter to the intention of the App not to use Locationservice...
                    // but we have to mitigate the Problem:
                    String release = Build.VERSION.RELEASE;
                    if (release.equals("6.0")) {
                        checkLocationServiceEnabled();
                    }
                }
            }
        };
        registerReceiver(receiver, i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUIRED_PERMISSIONS_REQUEST_CODE) {
            boolean permissionsGranted = true;
            for (int grantResult : grantResults) {
                permissionsGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (permissionsGranted) {
                if (getIDs().isEmpty()) {
                    firstRunScanAndShowWifi();
                }
            }
            //TODO: else { show explanation; }
        }
    }

    public Set<String> getIDs() {
        if (ids == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            ids = prefs.getStringSet(RULES_KEY, new HashSet<String>());
        }
        return ids;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return WifiSettingsFragment.class.equals(fragmentName) ||
                EventsSettingsFragment.class.equals(fragmentName);
    }

    public void onResume() {
        super.onResume();
//        datasource = new PreferenceDataSource(this);
//        datasource.open();
        getIDs();
    }

    @Override
    public void onPause() {
//        datasource.close();
        storeIDs();
        super.onPause();
    }

    private void storeIDs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(RULES_KEY, ids);
        editor.commit();
    }

    public static boolean addID(Activity activity, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> ids = prefs.getStringSet(RULES_KEY, new HashSet<String>());
        boolean success;
        if (ids.contains(id)) {
            success = false;
        } else {
            ids.add(id);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(RULES_KEY, ids);
            success = editor.commit();
        }
        return success;
    }

    @Override
    public void onSelectItem(int i) {
//        datasource.createWifiEvent(true, ssidsFoundArray[i], "", null, null,
//                datasource.getAllSoundProfiles().get(0L));
//        Preference p = new Preference(this);
//
//        String id = String.valueOf(p.hashCode());
//
//        p.setKey("ssid" + "_" + id);
//        p.setDefaultValue(ssidsFoundArray[i]);
//        p.setPersistent(true);
//        p.getEditor().commit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ssid_0", ssidsFoundArray[i]);
        editor.commit();

        getIDs().add("0");
        storeIDs();
        Fragment f = getFragmentManager().findFragmentById(R.id.main_content);
        if (f instanceof EventsSettingsFragment) {
            ((EventsSettingsFragment)f).buildUI();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkLocationServiceEnabled() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient.ConnectionCallbacks -> connected");
        if (mGoogleApiClient.getConnectionResult(LocationServices.API).isSuccess()) {
            LocationRequest justChecking = LocationRequest.create();
            LocationSettingsRequest.Builder builder =
                    new LocationSettingsRequest.Builder()
                            .addLocationRequest(justChecking);
            builder.setNeedBle(true);
            com.google.android.gms.common.api.PendingResult<LocationSettingsResult>
                    result =
                    LocationServices.SettingsApi
                            .checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied.

                            // So there were really no WIFIs available at Scan time
                            // this is OK - we won't bother creating a WIFI related setting now
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MuteSettingsActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            // That's bad  - is it possible to ask the user again when he is creating a WIFI-based rule himself?
                            // see http://stackoverflow.com/questions/29861580/locationservices-settingsapi-reset-settings-change-unavailable-flag
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient.ConnectionCallbacks -> connection suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient.OnConnectionFailedListener -> connectionFailed");
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        firstRunScanAndShowWifi();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }

    }

}
