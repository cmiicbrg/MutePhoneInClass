package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;

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
        implements FirstRunSSIDChooser.SSIDChosenListener {
    public static final String RULES_KEY = "rule_ids";
    private PreferenceDataSource datasource;
    private TreeMap<Long, WifiEvent> wifiEvents;
    private TreeMap<Long, EventProvider> eventProviders;
    private String[] ssidsFoundArray;
    private Set<String> ids;

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

        if (getIDs().isEmpty()) {
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

    }
}
