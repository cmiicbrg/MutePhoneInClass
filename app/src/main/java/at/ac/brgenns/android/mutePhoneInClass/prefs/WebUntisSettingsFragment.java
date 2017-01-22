package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class WebUntisSettingsFragment extends SettingsFragment {
    private static final String TAG = WebUntisSettingsFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.webuntis_event_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((WebUntisSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);

        root.addPreference(getEnablePreference(SettingKeys.WebUntis.ENABLE));
//        root.addPreference(getUsernamePasswordPreference());
        root.addPreference(getEnableWifiPreference(getString(R.string.mute_only_on_wifi),
                SettingKeys.Wifi.SSID));
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());
        PreferenceHelper.addID(getActivity(), id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, SettingKeys.Kusss.class);
    }

    private Preference getUsernamePasswordPreference() {
        UsernamePasswordPreference usernamePassword =
                new UsernamePasswordPreference(getActivity(), null);
        usernamePassword.setKey(SettingKeys.WebUntis.WEBUNTIS_USER + "_" + id);
        usernamePassword.setTitle(R.string.user_pass);
        usernamePassword.setDialogTitle(R.string.WebUntis);
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

}