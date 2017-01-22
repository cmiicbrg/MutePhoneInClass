package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import at.ac.brgenns.android.mutePhoneInClass.MutePhoneService;
import at.ac.brgenns.android.mutePhoneInClass.R;
import at.ac.brgenns.android.mutePhoneInClass.RuleTypeChooser;

/**
 * Created by Christoph on 27.05.2016.
 */
public class EventsSettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = EventsSettingsFragment.class.getSimpleName();

    private DurationPickerPreference pDisableFor;

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

        //add Enable/Disable Prefrence
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> IDs = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        final SwitchPreference pEnable = new SwitchPreference(getActivity());
        pEnable.setKey(SettingKeys.MUTE_ENABLED);
        pEnable.setTitle(R.string.enable);
        pEnable.setDefaultValue(true);
        pEnable.setOnPreferenceChangeListener(this);
        root.addPreference(pEnable);
        // TODO final Preference pDisableFor... if disabled it should be possible to set for how long

        pDisableFor = new DurationPickerPreference(getActivity(), null);

        pDisableFor.setIcon(R.drawable.ic_schedule_black_24dp);
        pDisableFor.setKey(SettingKeys.DISABLED_FOR);
//        pDisableFor.setTitle(R.string.disable_for);
        pDisableFor.setDialogTitle(R.string.disable_for);
        pDisableFor.setEnabled(!prefs.getBoolean(SettingKeys.MUTE_ENABLED, true));
        pDisableFor.setDefaultValue(0);
        pDisableFor.setOnPreferenceChangeListener(this);
        root.addPreference(pDisableFor);

        PreferenceCategory rules = new PreferenceCategory(getActivity());
        rules.setTitle("Rules");
        root.addPreference(rules);

        for (final String id : IDs) {
            String soundProfile_id = prefs.getString(
                    SettingKeys.Wifi.SOUND_PROFILE + "_" + id, "0");
            String soundProfileName = "";
            if (soundProfile_id == "0") {
                soundProfileName = getString(R.string.alarms_only);
            } else if (soundProfile_id == "1") {
                soundProfileName = getString(R.string.total_silence);
            } else {
                soundProfileName = prefs.getString(
                        SettingKeys.SoundProfile.RULE_NAME + "_" + soundProfile_id,
                        getString(R.string.alarms_only));
            }
//            if (prefs.contains(SettingKeys.Wifi.SSID + "_" + id)) {
            if (!prefs.contains(SettingKeys.Kusss.USER + "_" + id) &&
                    prefs.contains(SettingKeys.Wifi.SSID + "_" + id)) {
                final Preference p = new Preference(getActivity());
                p.setIcon(R.mipmap.ic_stat_name);
                p.setTitle(prefs.getString(SettingKeys.Wifi.RULE_NAME + "_" + id,
                        prefs.getString(SettingKeys.Wifi.SSID + "_" + id, "")));

                p.setSummary(soundProfileName);
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
                rules.addPreference(p);
            } else if (prefs.contains(SettingKeys.Kusss.USER + "_" + id)) {
                final Preference p = new Preference(getActivity());
                p.setIcon(R.drawable.ic_account_black_24dp);
                p.setTitle(getString(R.string.kusss) + " - " +
                        prefs.getString(SettingKeys.Kusss.USER + "_" + id, ""));
                p.setSummary(prefs.getString(SettingKeys.Wifi.SSID + "_" + id,
                        getString(R.string.ignore_wifi)) + ", " + soundProfileName);
                p.setPersistent(false);
                p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getActivity(), KusssSettingsActivity.class);
                        intent.putExtra(MuteSettingsActivity.SETTING_ID, id);
                        startActivity(intent);
                        return true;
                    }
                });
                rules.addPreference(p);
            }
        }

        final Preference pAdd = new Preference(getActivity());
        pAdd.setIcon(R.drawable.ic_add_black_24dp);
        pAdd.setTitle(R.string.add_rule);
        pAdd.setPersistent(false);
        pAdd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RuleTypeChooser chooseRuleType = new RuleTypeChooser();
                chooseRuleType.show(getFragmentManager(), "wekd");
//                ((MuteSettingsActivity) getActivity()).runScanAndShowWifi();
                return true;
            }
        });
        rules.addPreference(pAdd);

        PreferenceCategory soundCat = new PreferenceCategory(getActivity());
        soundCat.setTitle("Sound profiles");
        root.addPreference(soundCat);

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
        soundCat.addPreference(ps);

        final Preference pw = new Preference(getActivity());
        pw.setIcon(R.drawable.ic_warning_black_24dp);
//        pw.setTitle(R.string.sound_profile_manage);
        pw.setSummary(R.string.warning_sound_setting);
        pw.setSelectable(false);
        pw.setPersistent(false);

        soundCat.addPreference(pw);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(SettingKeys.MUTE_ENABLED)) {
            if (newValue.equals(Boolean.TRUE)) {
                Log.d(TAG, "muting will be enabled, enabling Receivers");
                Intent mutePhoneService = new Intent(getActivity(), MutePhoneService.class);
                mutePhoneService.putExtra(MutePhoneService.TASK, MutePhoneService.ENABLE);
                getActivity().startService(mutePhoneService);
                pDisableFor.setEnabled(false);
            } else {
                pDisableFor.setEnabled(true);
                pDisableFor.show();
            }
        }
        return true;
    }
}
