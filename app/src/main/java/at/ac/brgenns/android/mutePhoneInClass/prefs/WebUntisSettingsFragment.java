package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
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
    private Preference serverURLPreference;
    private Preference schoolNamePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.webuntis_event_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((WebUntisSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);

        root.addPreference(getEnablePreference(SettingKeys.WebUntis.ENABLE));
        root.addPreference(getServerURLPreference());
        root.addPreference(getSchoolNamePreference());
//        root.addPreference(getUseUsernameAndPasswordPreference("Use Username and Password."));
        root.addPreference(getUsernamePasswordPreference());
//        root.addPreference(getClassPreference());
        root.addPreference(getEnableWifiPreference(getString(R.string.mute_only_on_wifi)));
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());

        addNextEventPreference(root);

        PreferenceHelper.addID(getActivity(), id);
    }

//    private Preference getClassPreference() {
//        EditTextPreference className = new EditTextPreference(getActivity());
//        className.setKey(SettingKeys.WebUntis.CLASS_NAME + "_" + id);
//        className.setTitle(R.string.ClassName);
//
//        PreferenceHelper.bindPreferenceSummaryToValue(className);
//
//        return className;
//    }

    public Preference getServerURLPreference() {
        EditTextPreference serverURL = new EditTextPreference(getActivity());
        serverURL.setKey(SettingKeys.WebUntis.SERVER_URL + "_" + id);
        serverURL.setTitle(R.string.server_url);

        PreferenceHelper.bindPreferenceSummaryToValue(serverURL);
        return serverURL;
    }

    public Preference getSchoolNamePreference() {
        EditTextPreference schoolName = new EditTextPreference(getActivity());
        schoolName.setKey(SettingKeys.WebUntis.SCHOOL_NAME + "_" + id);
        schoolName.setTitle(R.string.school_name);

        PreferenceHelper.bindPreferenceSummaryToValue(schoolName);
        return schoolName;
    }

    protected SwitchPreference getUseUsernameAndPasswordPreference(String title) {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setPersistent(false);
        enable.setTitle(title);
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean defaultValue = prefs.getString(SettingKeys.WebUntis.USER + "_" + id, "") != "";
        enable.setDefaultValue(defaultValue);
        enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    boolean newBool = ((Boolean) newValue).booleanValue();
                    UsernamePasswordPreference p = (UsernamePasswordPreference) findPreference(
                            SettingKeys.WebUntis.USER + "_" + id);
                    p.setEnabled(newBool);
                    if (newBool) {
                        p.setSummary(getString(R.string.choose_wifi));
                    } else {
                        p.setSummary("");
                        prefs.edit().remove(SettingKeys.WebUntis.USER + "_" + id).commit();
                    }
                }
                return true;
            }
        });
        return enable;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, SettingKeys.WebUntis.class);
    }

    private Preference getUsernamePasswordPreference() {
        UsernamePasswordPreference usernamePassword =
                new UsernamePasswordPreference(getActivity(), null);
        usernamePassword.setKey(SettingKeys.WebUntis.USER + "_" + id);
        usernamePassword.setTitle(R.string.user_pass);
        usernamePassword.setDialogTitle(R.string.WebUntis);
        PreferenceHelper.bindPreferenceSummaryToValue(usernamePassword);
        return usernamePassword;
    }


}