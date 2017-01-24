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
public class KusssSettingsFragment extends SettingsFragment {
    private static final String TAG = KusssSettingsFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kusss_event_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((KusssSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);

        root.addPreference(getEnablePreference(SettingKeys.Kusss.ENABLE));
        root.addPreference(getUsernamePasswordPreference());
        root.addPreference(getEnableWifiPreference(getString(R.string.mute_only_on_wifi)));
        root.addPreference(getSSIDChooserPreference());
        root.addPreference(getSoundProfilePreference());

        addNextEventPreference(root);

        PreferenceHelper.addID(getActivity(), id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item, SettingKeys.Kusss.class);
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

}