package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;

import java.util.List;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class WifiSettingsFragment extends PreferenceFragment {
    String id = "1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_event_settings);
        final PreferenceScreen root = getPreferenceScreen();

        root.addPreference(getEnablePreference());
        root.addPreference(getRuleNamePreference());
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());

        MuteSettingsActivity.addID(getActivity(), id);
    }

    @NonNull
    private SwitchPreference getEnablePreference() {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setKey("enable" + "_" + id);
        enable.setTitle("On");
        enable.setDefaultValue(true);
        return enable;
    }

    @NonNull
    private EditTextPreference getRuleNamePreference() {
        EditTextPreference name = new EditTextPreference(getActivity());
        name.setKey("rule_name" + "_" + id);
        name.setTitle(R.string.rule_name_title);
        name.setDefaultValue(getString(R.string.rule_name_default));
        MuteSettingsActivity.bindPreferenceSummaryToValue(name);
        return name;
    }

    @NonNull
    private ListPreference getSSIDChooserPreference() {
        ListPreference ssid = new ListPreference(getActivity());
        ssid.setKey("ssid" + "_" + id);
        ssid.setTitle(R.string.mute_on_wifi);
        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifi.startScan();
        List<ScanResult> wifisFoundList = wifi.getScanResults();
        String[] ssidsFoundArray = new String[wifisFoundList.size()];
//        ssidsFoundArray[0] = ssid.getValue();
        for (int i = 0; i < wifisFoundList.size(); i++) {
            ssidsFoundArray[i] = wifisFoundList.get(i).SSID;
        }
        ssid.setEntries(ssidsFoundArray);
        ssid.setEntryValues(ssidsFoundArray);
        MuteSettingsActivity.bindPreferenceSummaryToValue(ssid);
        return ssid;
    }

    @NonNull
    private ListPreference getSoundProfilePreference() {
        ListPreference soundProfile = new ListPreference(getActivity());
        soundProfile.setKey("soundProfile" + "_" + id);
        soundProfile.setTitle(R.string.sound_profile_title);
        soundProfile.setEntries(R.array.sound_profiles);
        soundProfile.setEntryValues(R.array.listvalues);
        soundProfile.setDefaultValue("0");
        MuteSettingsActivity.bindPreferenceSummaryToValue(soundProfile);
        return soundProfile;
    }

}