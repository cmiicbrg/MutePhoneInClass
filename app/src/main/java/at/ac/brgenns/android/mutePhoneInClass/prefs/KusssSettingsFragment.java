package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import at.ac.brgenns.android.mutePhoneInClass.MutePhoneService;
import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class KusssSettingsFragment extends PreferenceFragment {
    private static final String TAG = KusssSettingsFragment.class.getSimpleName();
    String id = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sound_profile_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((KusssSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);

        root.addPreference(getEnablePreference());
        root.addPreference(getUsernamePasswordPreference());
        root.addPreference(getEnableWifiPreference(getString(R.string.mute_only_on_wifi),
                SettingKeys.Wifi.SSID));
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());
        PreferenceHelper.addID(getActivity(), id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case android.R.id.home:
                //TODO: check if Preferences are OK and delete already saved Preferences if not
                getActivity().onBackPressed();
                return true;
            case R.id.action_delete:
                // TODO: should we show a warning?
                PreferenceHelper.deleteRule(getActivity(), id, SettingKeys.Kusss.class);
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @NonNull
    private SwitchPreference getEnablePreference() {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setKey(SettingKeys.Kusss.ENABLE + "_" + id);
        enable.setTitle(R.string.rule_enabled);
        enable.setDefaultValue(true);
        return enable;
    }

    private Preference getUsernamePasswordPreference() {
        UsernamePasswordPreference usernamePassword =
                new UsernamePasswordPreference(getActivity(), null);
        usernamePassword.setKey(SettingKeys.Kusss.USER + "_" + id);
        usernamePassword.setTitle(R.string.user_pass);
        usernamePassword.setDialogTitle(R.string.KUSSS);
        PreferenceHelper.bindPreferenceSummaryToValue(usernamePassword);

        return usernamePassword;
    }

    private SwitchPreference getEnableWifiPreference(String title,
                                                     final SettingKeys.Wifi key) {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setPersistent(false);
        enable.setKey("ENABLE_" + key + "_" + id);
        enable.setTitle(title);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean defaultValue = prefs.getString(key + "_" + id, "") != "";
        enable.setDefaultValue(defaultValue);
        enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    boolean newBool = ((Boolean) newValue).booleanValue();
                    ListPreference p = (ListPreference) findPreference(key + "_" + id);
                    p.setEnabled(newBool);
                    if (newBool) {
                        p.setSummary(getString(R.string.choose_wifi));
                    } else {
                        p.setSummary("");
                        prefs.edit().remove(key + "_" + id).commit();
                    }
                }
                return true;
            }
        });
        return enable;
    }

    @NonNull
    private ListPreference getSSIDChooserPreference() {
        ListPreference ssid = new ListPreference(getActivity());
        ssid.setKey(SettingKeys.Wifi.SSID + "_" + id);
        ssid.setTitle(R.string.mute_on_wifi);
        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        Set<String> SSIDStringSet = new HashSet<>();
        SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ssid.getContext());
        if (defaultSharedPreferences.contains(ssid.getKey())) {
            SSIDStringSet.add(defaultSharedPreferences
                    .getString(ssid.getKey(), ""));
        }
        SSIDStringSet
                .addAll(MutePhoneService.scanResultToUniqueSSIDStringSet(wifi.getScanResults()));
        SSIDStringSet.addAll(MutePhoneService
                .configuredNetworksToUniqueSSIDStringSet(wifi.getConfiguredNetworks()));
        String[] ssidsFoundArray = new String[SSIDStringSet.size()];
        SSIDStringSet.toArray(ssidsFoundArray);
        ssid.setEntries(ssidsFoundArray);
        ssid.setEntryValues(ssidsFoundArray);
        PreferenceHelper.bindPreferenceSummaryToValue(ssid);
        if (ssid.getSummary() == null || ssid.getSummary().toString().isEmpty()) {
//            ssid.setSummary(getString(R.string.ignore_wifi));
            ssid.setEnabled(false);
        }
        return ssid;
    }

    @NonNull
    private ListPreference getSoundProfilePreference() {
        ListPreference soundProfile = new ListPreference(getActivity());
        soundProfile.setKey(SettingKeys.Wifi.SOUND_PROFILE + "_" + id);
        soundProfile.setTitle(R.string.sound_profile_title);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> IDs = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        String[] entries = new String[IDs.size() + 2];
        String[] values = new String[IDs.size() + 2];
        int i = 0;
        entries[i] = getString(R.string.alarms_only);
        values[i++] = "0";
        entries[i] = getString(R.string.total_silence);
        values[i++] = "1";

        for (final String id : IDs) {
            if (prefs.contains(SettingKeys.SoundProfile.RINGER_VOLUME + "_" + id)) {
                entries[i] = prefs.getString(SettingKeys.SoundProfile.RULE_NAME + "_" + id,
                        getString(R.string.rule_name_default));
                values[i++] = id;
            }
        }

        soundProfile.setEntries(Arrays.copyOfRange(entries, 0, i));
        soundProfile.setEntryValues(Arrays.copyOfRange(values, 0, i));
        soundProfile.setDefaultValue("0");

        PreferenceHelper.bindPreferenceSummaryToValue(soundProfile);
        if (soundProfile.getSummary() == null || soundProfile.getSummary().toString().isEmpty()) {
            soundProfile.setSummary(getString(R.string.alarms_only));
        }
        return soundProfile;
    }
}