package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import at.ac.brgenns.android.mutePhoneInClass.MutePhoneService;
import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class EventsSettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = EventsSettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mute_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        buildUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void buildUI() {
        final PreferenceScreen root = getPreferenceScreen();
        root.removeAll();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> IDs = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        final SwitchPreference pEnable = new SwitchPreference(getActivity());
        pEnable.setKey(SettingKeys.MUTE_ENABLED);
        pEnable.setTitle(R.string.enable);
        pEnable.setDefaultValue(true);
        pEnable.setOnPreferenceChangeListener(this);
        root.addPreference(pEnable);
        // TODO final Preference pDisableFor... if disabled it should be possible to set for how long

        for (final String id : IDs) {
            if (prefs.contains(SettingKeys.Wifi.SSID + "_" + id)) {
                final Preference p = new Preference(getActivity());
                p.setIcon(R.mipmap.ic_stat_name);
                p.setTitle(prefs.getString(SettingKeys.Wifi.SSID + "_" + id, ""));
                String soundProfile_id = prefs.getString(
                        SettingKeys.Wifi.SOUND_PROFILE + "_" + id, "0");
                p.setSummary(getResources().getStringArray(R.array.sound_profiles)[Integer
                        .parseInt(soundProfile_id)]);
                p.setPersistent(false);
                p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getActivity(), WifiSettingsActivity.class);
                        intent.putExtra(MuteSettingsActivity.SETTING_ID, id);
                        startActivity(intent);
                        return true;
                    }
                });
                root.addPreference(p);
            }
        }

        //TODO add Eventproviders
        final Preference p = new Preference(getActivity());
        p.setIcon(R.drawable.ic_add_black_24dp);
        p.setTitle(R.string.add_rule);
        p.setPersistent(false);
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                ((MuteSettingsActivity) getActivity()).runScanAndShowWifi();
//                Intent intent = new Intent(getActivity(), WifiSettingsActivity.class);
//                intent.putExtra(MuteSettingsActivity.SETTING_ID, id);
//                startActivity(intent);

//                WifiSettingsFragment fragment = new WifiSettingsFragment();
//                fragment.id = String.valueOf(p.hashCode());
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.main_content, fragment).commit();
                return true;
            }
        });
        root.addPreference(p);

        final Preference ps = new Preference(getActivity());
        ps.setIcon(R.drawable.ic_volume_off_black_24dp);
        ps.setTitle(R.string.sound_profile_manage);
        ps.setPersistent(false);
        ps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SoundProfileManageActivity.class);
                startActivity(intent);
                return true;
            }
        });
        root.addPreference(ps);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(SettingKeys.MUTE_ENABLED)) {
            if (newValue.equals(Boolean.TRUE)) {
                Log.d(TAG, "muting will be enabled, enabling Receivers");
                Intent mutePhoneService = new Intent(getActivity(), MutePhoneService.class);
                mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.ENABLE);
                getActivity().startService(mutePhoneService);
            } else {
                Log.d(TAG, "muting will be disabled, disabling Receivers");
                Intent mutePhoneService = new Intent(getActivity(), MutePhoneService.class);
                mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.DISABLE);
                getActivity().startService(mutePhoneService);
            }
        }
        return true;
    }
}
