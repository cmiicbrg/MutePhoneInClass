package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.util.HashSet;
import java.util.Set;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class EventsSettingsFragment extends PreferenceFragment {
//    private PreferenceDataSource datasource;
//    private TreeMap<Long, WifiEvent> wifiEvents;
//    private TreeMap<Long, EventProvider> eventProviders;
//    private String[] ssidsFoundArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mute_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
//        datasource = new PreferenceDataSource(getActivity());
//        datasource.open();
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
//        datasource.close();
        super.onPause();
    }

    protected void buildUI() {
        final PreferenceScreen root = getPreferenceScreen();
        root.removeAll();
//        wifiEvents = datasource.getAllWifiEvents();
//        eventProviders = datasource.getAllEventProviders();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> IDs = prefs.getStringSet(MuteSettingsActivity.RULES_KEY, new HashSet<String>());

        for (final String id : IDs) {
            final Preference p = new Preference(getActivity());
            p.setIcon(R.mipmap.ic_stat_name);
            p.setTitle(prefs.getString("ssid" + "_" + id, ""));
            String soundProfile_id = prefs.getString("soundProfile" + "_" + id, "0");
            p.setSummary(getResources().getStringArray(R.array.sound_profiles)[Integer
                    .parseInt(soundProfile_id)]);
            p.setPersistent(false);
            p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    WifiSettingsFragment fragment = new WifiSettingsFragment();
                    fragment.id = id;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.main_content, fragment).commit();
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
                WifiSettingsFragment fragment = new WifiSettingsFragment();
                fragment.id = String.valueOf(p.hashCode());
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, fragment).commit();
                return true;
            }
        });
        root.addPreference(p);
    }
}
