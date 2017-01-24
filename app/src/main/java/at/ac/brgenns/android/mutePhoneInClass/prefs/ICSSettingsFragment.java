package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class ICSSettingsFragment extends SettingsFragment {
    private static final String TAG = ICSSettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ics_event_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((ICSSettingsActivity) getActivity()).getSettingID();

        root.addPreference(getEnablePreference(SettingKeys.ICS.ENABLE));

        setHasOptionsMenu(true);
        EditTextPreference p = new EditTextPreference(getActivity());
        p.setTitle(getString(R.string.iCalendar_address));
        p.setKey(SettingKeys.ICS.ICS_URL + "_" + id);
        root.addPreference(p);

        root.addPreference(getEnableWifiPreference(getString(R.string.mute_only_on_wifi),
                SettingKeys.Wifi.SSID));
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());

        addNextEventPreference(root);

        PreferenceHelper.addID(getActivity(), id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, SettingKeys.ICS.class);
    }



}