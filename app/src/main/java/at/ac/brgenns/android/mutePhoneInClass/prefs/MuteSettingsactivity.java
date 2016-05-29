package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;
import java.util.TreeMap;

import at.ac.brgenns.android.mutePhoneInClass.FirstRunSSIDChooser;
import at.ac.brgenns.android.mutePhoneInClass.R;
import at.ac.brgenns.android.mutePhoneInClass.prefs.db.PreferenceDataSource;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.EventProvider;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.WifiEvent;

public class MuteSettingsActivity extends PreferenceActivity implements FirstRunSSIDChooser.SSIDChosenListener {
    private PreferenceDataSource datasource;
    private TreeMap<Long, WifiEvent> wifiEvents;
    private TreeMap<Long, EventProvider> eventProviders;
    private String[] ssidsFoundArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        datasource = new PreferenceDataSource(this);
        datasource.open();
        wifiEvents = datasource.getAllWifiEvents();
        eventProviders = datasource.getAllEventProviders();
        if (wifiEvents.size() == 0 && eventProviders.size() == 0) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifi.startScan();
            List<ScanResult> wifisFoundList = wifi.getScanResults();

            ssidsFoundArray = new String[wifisFoundList.size()];
            for (int i = 0; i < wifisFoundList.size(); i++) {
                ssidsFoundArray[i] = wifisFoundList.get(i).SSID;
            }
            FirstRunSSIDChooser firstRunSSIDChooser = new FirstRunSSIDChooser();
            firstRunSSIDChooser.setOptions(ssidsFoundArray);
            firstRunSSIDChooser.show(getFragmentManager(), "dosth");
        }
        setContentView(R.layout.activity_mute_settings);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return WifiSettingsFragment.class.equals(fragmentName) ||
                EventsSettingsFragment.class.equals(fragmentName);
    }

    public void onResume() {
        super.onResume();
        datasource = new PreferenceDataSource(this);
        datasource.open();
    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public void onSelectItem(int i) {
        datasource.createWifiEvent(true, ssidsFoundArray[i], "", null, null,
                datasource.getAllSoundProfiles().get(0L));
    }
}
