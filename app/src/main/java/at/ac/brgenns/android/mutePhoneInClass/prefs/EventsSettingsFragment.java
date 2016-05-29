package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.TreeMap;

import at.ac.brgenns.android.mutePhoneInClass.FirstRunSSIDChooser;
import at.ac.brgenns.android.mutePhoneInClass.R;
import at.ac.brgenns.android.mutePhoneInClass.prefs.db.PreferenceDataSource;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.EventProvider;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.WifiEvent;

/**
 * Created by Christoph on 27.05.2016.
 */
public class EventsSettingsFragment extends PreferenceFragment implements
        FirstRunSSIDChooser.SSIDChosenListener {
    private PreferenceDataSource datasource;
    private TreeMap<Long, WifiEvent> wifiEvents;
    private TreeMap<Long, EventProvider> eventProviders;
    private String[] ssidsFoundArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mute_settings);

    }

    @Override
    public void onResume() {
        super.onResume();
        datasource = new PreferenceDataSource(getActivity());
        datasource.open();
//        wifiEvents = datasource.getAllWifiEvents();
//        eventProviders = datasource.getAllEventProviders();
//        if (wifiEvents.size() == 0 && eventProviders.size() == 0) {
//            WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
//            wifi.startScan();
//            List<ScanResult> wifisFoundList = wifi.getScanResults();
//
//            ssidsFoundArray = new String[wifisFoundList.size()];
//            for (int i = 0; i < wifisFoundList.size(); i++) {
//                ssidsFoundArray[i] = wifisFoundList.get(i).SSID;
//            }
//            FirstRunSSIDChooser dialog = new FirstRunSSIDChooser();
//            dialog.setOptions(ssidsFoundArray);
//            dialog.show(getFragmentManager(), "dosth");
//        }
        buildUI();
    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }

    private void buildUI() {
        final PreferenceScreen root = getPreferenceScreen();
        root.removeAll();
        wifiEvents = datasource.getAllWifiEvents();
        eventProviders = datasource.getAllEventProviders();
        for (WifiEvent wifiEvent : wifiEvents.values()) {
            final Preference p = new Preference(getActivity());
            p.setIcon(R.drawable.ic_stat_name);
            p.setTitle(wifiEvent.getSSID());
            p.setSummary(wifiEvent.getSoundProfile().getName());
            p.setPersistent(false);
            p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.main_content, new WifiSettingsFragment()).commit();
//                    getContext().startActivity(new Intent(WifiSettingsFragment.class));
                    return true;
                }
            });
            root.addPreference(p);
        }
        //TODO add Eventproviders
        final Preference p = new Preference(getActivity());
        p.setIcon(R.drawable.ic_add_black_24dp);
        p.setTitle(R.string.add_rule);
        p.setPersistent(false);
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, new WifiSettingsFragment()).commit();
                return true;
            }
        });
        root.addPreference(p);
    }

    @Override
    public void onSelectItem(int i) {
        //TODO
        datasource.createWifiEvent(true, ssidsFoundArray[i], "", null, null,
                datasource.getAllSoundProfiles().get(0));
        buildUI();
    }
}
