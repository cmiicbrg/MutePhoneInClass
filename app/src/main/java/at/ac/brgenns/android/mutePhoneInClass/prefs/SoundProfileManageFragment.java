package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import at.ac.brgenns.android.mutePhoneInClass.R;

public class SoundProfileManageFragment extends PreferenceFragment {

    private static final String TAG = SoundProfileSettingsFragment.class.getSimpleName();
    //    String id = "0";
    private AudioManager audioManager;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sound_profile_settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

//        final PreferenceScreen root = getPreferenceScreen();

//        id = ((SoundProfileSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);
//        buildUI();

    }

    @Override
    public void onResume() {
        super.onResume();
        buildUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void buildUI() {
        final PreferenceScreen root = getPreferenceScreen();
        root.removeAll();
        final Preference p0 = new Preference(getActivity());
        p0.setIcon(R.drawable.ic_volume_off_black_24dp);
        p0.setPersistent(false);
        p0.setTitle(R.string.alarms_only);

        p0.setSummary(getString(R.string.media_volume) + ": " +
                getVolumeString(SettingKeys.SoundProfile.MEDIA_VOLUME, "0", 0) + ", " +
                getString(R.string.alarm_volume) + ": " +
                getVolumeString(SettingKeys.SoundProfile.ALARM_VOLUME, "0", -1) + ", " +
                getString(R.string.ringtone_volume) + ": " +
                getVolumeString(SettingKeys.SoundProfile.RINGER_VOLUME, "0", 0));
        root.addPreference(p0);
        final Preference p1 = new Preference(getActivity());
        p1.setIcon(R.drawable.ic_volume_off_black_24dp);
        p1.setPersistent(false);
        p1.setTitle(R.string.total_silence);

        p1.setSummary(getString(R.string.media_volume) + ": " +
                getVolumeString(SettingKeys.SoundProfile.MEDIA_VOLUME, "1", 0) + ", " +
                getString(R.string.alarm_volume) + ": " +
                getVolumeString(SettingKeys.SoundProfile.ALARM_VOLUME, "1", 0) + ", " +
                getString(R.string.ringtone_volume) + ": " +
                getVolumeString(SettingKeys.SoundProfile.RINGER_VOLUME, "1", 0));
        root.addPreference(p1);

        Set<String> IDs = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        for (final String id : IDs) {
            if (prefs.contains(SettingKeys.SoundProfile.RINGER_VOLUME + "_" + id)) {
                final Preference p = new Preference(getActivity());
                p.setIcon(R.drawable.ic_volume_off_black_24dp);
                p.setTitle(prefs.getString(SettingKeys.SoundProfile.RULE_NAME + "_" + id,
                        getString(R.string.rule_name_default)));
                p.setSummary(getString(R.string.media_volume) + ": " +
                        getVolumeString(SettingKeys.SoundProfile.MEDIA_VOLUME, id, -1) + ", " +
                        getString(R.string.alarm_volume) + ": " +
                        getVolumeString(SettingKeys.SoundProfile.ALARM_VOLUME, id, -1) + ", " +
                        getString(R.string.ringtone_volume) + ": " +
                        getVolumeString(SettingKeys.SoundProfile.RINGER_VOLUME, id, 0));
                p.setPersistent(false);
                p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent =
                                new Intent(getActivity(), SoundProfileSettingsActivity.class);
                        intent.putExtra(MuteSettingsActivity.SETTING_ID, id);
                        startActivity(intent);
                        return true;
                    }
                });
                root.addPreference(p);
            }
        }

        final Preference ps = new Preference(getActivity());
        ps.setIcon(R.drawable.ic_add_black_24dp);
        ps.setTitle(R.string.add_sound_profile);
        ps.setPersistent(false);
        ps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String uid = UUID.randomUUID().toString();
                PreferenceHelper.addID(getActivity(), uid);
                Intent intent = new Intent(getActivity(), SoundProfileSettingsActivity.class);
                intent.putExtra(MuteSettingsActivity.SETTING_ID, uid);
                startActivity(intent);
                return true;
            }
        });
        root.addPreference(ps);
    }

    private void startProfileSettingsActivity(String profileID) {
        Intent intent = new Intent(getActivity(), SoundProfileSettingsActivity.class);
        intent.putExtra(MuteSettingsActivity.SETTING_ID, profileID);
        startActivity(intent);
    }

    private String getVolumeString(SettingKeys.SoundProfile key, String id, int defaultValue) {
        int vol = prefs.getInt(key + "_" + id, defaultValue);
        String volText = "";
        if (vol < 0) {
            volText = "No change";
        } else if (vol == 0) {
            volText = "Mute";
        } else {
            volText = String.valueOf(vol);
        }
        return volText;
    }

}
