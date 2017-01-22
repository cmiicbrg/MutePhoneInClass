package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class WifiSettingsFragment extends SettingsFragment {
    private static final String TAG = WifiSettingsFragment.class.getSimpleName();
//    String id = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_event_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((WifiSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);

        root.addPreference(getEnablePreference(SettingKeys.Wifi.ENABLE));
        root.addPreference(getRuleNamePreference());
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());

        PreferenceHelper.addID(getActivity(), id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, SettingKeys.Wifi.class);
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

}