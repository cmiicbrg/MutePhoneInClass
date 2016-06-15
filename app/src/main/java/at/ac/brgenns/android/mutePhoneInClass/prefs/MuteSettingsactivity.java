package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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
import java.util.UUID;

import at.ac.brgenns.android.mutePhoneInClass.SSIDChooser;
import at.ac.brgenns.android.mutePhoneInClass.MutePhoneService;
import at.ac.brgenns.android.mutePhoneInClass.R;

public class MuteSettingsActivity extends AppCompatPreferenceActivity
        implements SSIDChooser.SSIDChosenListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MuteSettingsActivity.class.getSimpleName();

    private static final int REQUIRED_PERMISSIONS_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    public static final String SETTING_ID = "Setting_ID";
    public static final String[] PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE};

    public GoogleApiClient mGoogleApiClient;
    private String[] ssidsFoundArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mute_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasPermissionsToScanWifi()) {
            //TODO: 06-13 10:04:48.123 12499-12499/at.ac.brgenns.android.mutePhoneInClass W/Activity: Can reqeust only one set of permissions at a time
            requestPermissions(PERMISSIONS,
                    REQUIRED_PERMISSIONS_REQUEST_CODE);
        } else if (getIDs().isEmpty() && isMutingEnabled()) {
            runScanAndShowWifi();
        }
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

    protected void runScanAndShowWifi() {
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
                    Set<String> SSIDStringSet =
                            MutePhoneService.scanResultToUniqueSSIDStringSet(wifisFoundList);
                    ssidsFoundArray = new String[SSIDStringSet.size()];
                    SSIDStringSet.toArray(ssidsFoundArray);
                    SSIDChooser SSIDChooser = new SSIDChooser();
                    SSIDChooser.setOptions(ssidsFoundArray);
                    SSIDChooser.show(getFragmentManager(), "dosth");
                } else {
                    // There seems to be a bug in 6.0 and up https://code.google.com/p/android/issues/detail?id=185370
                    // this runs counter to the intention of the App not to use Locationservice...
                    // but we have to mitigate the Problem:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                if (getIDs().isEmpty() && isMutingEnabled()) {
                    runScanAndShowWifi();
                }
            }
            // TODO: else { show explanation; }
        }
    }

    public final Set<String> getIDs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> ids = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        return ids;
    }

    public boolean isMutingEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(SettingKeys.MUTE_ENABLED, true);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return WifiSettingsFragment.class.equals(fragmentName) ||
                EventsSettingsFragment.class.equals(fragmentName);
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSelectItem(int i) {
        String uid = UUID.randomUUID().toString();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingKeys.Wifi.SSID + "_" + uid, ssidsFoundArray[i]);
        editor.commit();

        PreferenceHelper.addID(this, uid);

        // Refresh the view
        Fragment f = getFragmentManager().findFragmentById(R.id.main_content);
        if (f instanceof EventsSettingsFragment) {
            ((EventsSettingsFragment) f).buildUI();
        }

        // Start the Service -- Always set the WIFI_RULE_ADDED flag if WIFI-based rule is added.
        Intent mutePhoneService = new Intent(this, MutePhoneService.class);
        mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.WIFI_RULE_ADDED);
        startService(mutePhoneService);
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
                        runScanAndShowWifi();

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
