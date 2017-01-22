package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
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
 * Created by Christoph on 22.01.2017.
 */

public class SettingsFragment extends PreferenceFragment {
    String id = "0";

    @NonNull
    protected SwitchPreference getEnablePreference(Enum keys) {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setKey(keys + "_" + id);
        enable.setTitle(R.string.rule_enabled);
        enable.setDefaultValue(true);
        return enable;
    }

    @NonNull
    protected ListPreference getSSIDChooserPreference() {
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
    protected ListPreference getSoundProfilePreference() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item, Class keysClass) {
        int itemID = item.getItemId();
        int R_id_action_delete = R.id.action_delete;
        switch (itemID) {
            case android.R.id.home:
                //TODO: check if Preferences are OK and delete already saved Preferences if not
                getActivity().onBackPressed();
                return true;
            case R.id.action_delete:
                // TODO: should we show a warning?
                PreferenceHelper.deleteRule(getActivity(), id, keysClass);
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
