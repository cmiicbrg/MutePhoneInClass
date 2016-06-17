package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class SoundProfileSettingsFragment extends PreferenceFragment {
    private static final String TAG = SoundProfileSettingsFragment.class.getSimpleName();
    String id = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sound_profile_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((SoundProfileSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);

//        root.addPreference(getEnablePreference());
//        root.addPreference(getRuleNamePreference());
//        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());

        PreferenceHelper.addID(getActivity(), id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        int R_id_action_delete = R.id.action_delete;
        switch (itemID) {
            case android.R.id.home:
                //TODO: check if Preferences are OK and delete already saved Preferences if not
                getActivity().onBackPressed();
                return true;
            case R.id.action_delete:
                // TODO: should we show a warning?
                PreferenceHelper.deleteRule(getActivity(), id, SettingKeys.SoundProfile.class);
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (id != "0" && id != "1") {
            inflater.inflate(R.menu.menu_delete, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @NonNull
    private SwitchPreference getEnablePreference() {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setKey(SettingKeys.Wifi.ENABLE + "_" + id);
        enable.setTitle(R.string.rule_enabled);
        enable.setDefaultValue(true);
        return enable;
    }

    @NonNull
    private EditTextPreference getRuleNamePreference() {
        EditTextPreference name = new EditTextPreference(getActivity());
        name.setKey(SettingKeys.Wifi.RULE_NAME + "_" + id);
        name.setTitle(R.string.rule_name_title);
        name.setDefaultValue(getString(R.string.rule_name_default));
        PreferenceHelper.bindPreferenceSummaryToValue(name);
        return name;
    }

    @NonNull
    private ListPreference getSSIDChooserPreference() {
        ListPreference ssid = new ListPreference(getActivity());
        ssid.setKey(SettingKeys.Wifi.SSID + "_" + id);
        ssid.setTitle(R.string.mute_on_wifi);
        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
//        wifi.startScan();
        //TODO: when editing a setting we don't want to search for WIFIS or add the old WIFI to the list
        //TODO: Use a receiver...
        //TODO: on 6.0 check if we have to enable Locationservice
        List<ScanResult> wifisFoundList = wifi.getScanResults();
        String[] ssidsFoundArray = new String[wifisFoundList.size()];
//        ssidsFoundArray[0] = ssid.getValue();
        for (int i = 0; i < wifisFoundList.size(); i++) {
            ssidsFoundArray[i] = wifisFoundList.get(i).SSID;
        }
        ssid.setEntries(ssidsFoundArray);
        ssid.setEntryValues(ssidsFoundArray);
        PreferenceHelper.bindPreferenceSummaryToValue(ssid);
        return ssid;
    }

    @NonNull
    private RingtonePreference getSoundProfilePreference() {
        RingtonePreference soundProfile = new RingtonePreference(getActivity());
        soundProfile.setKey(SettingKeys.Wifi.SOUND_PROFILE + "_" + id);
        soundProfile.setTitle(R.string.sound_profile_title);
//        soundProfile.setEntries(R.array.sound_profiles);
//        soundProfile.setEntryValues(R.array.listvalues);
//        soundProfile.setDefaultValue("0");
//        PreferenceHelper.bindPreferenceSummaryToValue(soundProfile);
        return soundProfile;
    }

}