package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
        root.addPreference(getKusssNamePreference());
        root.addPreference(getUsernamePasswordPreference());

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

    @NonNull
    private EditTextPreference getKusssNamePreference() {
        EditTextPreference name = new EditTextPreference(getActivity());
        name.setKey(SettingKeys.Kusss.RULE_NAME + "_" + id);
        name.setTitle(R.string.kusss);
//        name.setPersistent(false);
        return name;
    }

    private Preference getUsernamePasswordPreference() {
        UsernamePasswordPreference usernamePassword =
                new UsernamePasswordPreference(getActivity(), null);
        usernamePassword.setKey(SettingKeys.Kusss.USER + "_" + id);
        usernamePassword.setTitle(R.string.user_pass);
        usernamePassword.setDialogTitle(R.string.KUSSS);
        return usernamePassword;
    }

}