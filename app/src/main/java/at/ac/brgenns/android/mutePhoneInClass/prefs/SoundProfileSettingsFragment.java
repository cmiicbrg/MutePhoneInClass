package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pavelsikun.seekbarpreference.SeekBarPreference;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 27.05.2016.
 */
public class SoundProfileSettingsFragment extends PreferenceFragment {
    private static final String TAG = SoundProfileSettingsFragment.class.getSimpleName();
    String id = "0";
    private AudioManager audioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sound_profile_settings);
        final PreferenceScreen root = getPreferenceScreen();

        id = ((SoundProfileSettingsActivity) getActivity()).getSettingID();

        setHasOptionsMenu(true);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        root.addPreference(getSoundProfileNamePreference());

        root.addPreference(getEnableVolumePreference(getString(R.string.enable_media_volume_change),
                SettingKeys.SoundProfile.MEDIA_VOLUME, -1));
        root.addPreference(
                getSoundProfilePreference(getString(R.string.media_volume),
                        SettingKeys.SoundProfile.MEDIA_VOLUME,
                        -1, AudioManager.STREAM_MUSIC));

        root.addPreference(getEnableVolumePreference(getString(R.string.enable_alarm_volume_change),
                SettingKeys.SoundProfile.ALARM_VOLUME, -1));
        root.addPreference(
                getSoundProfilePreference(getString(R.string.alarm_volume),
                        SettingKeys.SoundProfile.ALARM_VOLUME,
                        -1, AudioManager.STREAM_ALARM));

        root.addPreference(
                getEnableVolumePreference(getString(R.string.enable_ringtone_volume_change),
                        SettingKeys.SoundProfile.RINGER_VOLUME, 0));
        root.addPreference(
                getSoundProfilePreference(getString(R.string.ringtone_volume),
                        SettingKeys.SoundProfile.RINGER_VOLUME,
                        0, AudioManager.STREAM_RING));

        root.addPreference(getVibrateEnablePreference());

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
    private EditTextPreference getSoundProfileNamePreference() {
        EditTextPreference name = new EditTextPreference(getActivity());
        name.setKey(SettingKeys.Wifi.RULE_NAME + "_" + id);
        name.setTitle(R.string.sound_profile_name);
        name.setDefaultValue(getString(R.string.new_sound_profile_name));

        PreferenceHelper.bindPreferenceSummaryToValue(name);
        if (name.getSummary().toString().isEmpty()) {
            name.setSummary(getString(R.string.new_sound_profile_name));
        }
        return name;
    }

    @NonNull
    private SwitchPreference getEnableVolumePreference(String title,
                                                       final SettingKeys.SoundProfile key,
                                                       final int defaultVolume) {
        SwitchPreference enable = new SwitchPreference(getActivity());
//        String keyStr = "ENABLE_" + key + "_" + id;
        enable.setPersistent(false);
        enable.setKey("ENABLE_" + key + "_" + id);
        enable.setTitle(title);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean defaultValue = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getInt(key + "_" + id, defaultVolume) >= 0;
        enable.setDefaultValue(defaultValue);
        enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue instanceof Boolean) {
                    boolean newBool = ((Boolean) newValue).booleanValue();
                    SeekBarPreference p = (SeekBarPreference) findPreference(key + "_" + id);

                    if (newBool) {
                        p.setEnabled(newBool);
                        p.setMinValue(0);
                        p.setCurrentValue(defaultVolume);
                    } else {
                        p.setMinValue(-1);
                        p.setCurrentValue(-1);
                        p.setEnabled(newBool);
                    }
                }
                return true;
            }
        });

        return enable;
    }

    @NonNull
    private SeekBarPreference getSoundProfilePreference(String title, SettingKeys.SoundProfile key,
                                                        int defaultValue, int streamType) {
        SeekBarPreference volume = new SeekBarPreference(getActivity());
        volume.setKey(key + "_" + id);
        volume.setTitle(title);
//        soundProfile.setEntries(R.array.sound_profiles);
////        soundProfile.setEntryValues(R.array.listvalues);

        volume.setMinValue(defaultValue < 0 ? -1 : 0);
        volume.setMaxValue(audioManager.getStreamMaxVolume(streamType));
        volume.setDialogEnabled(false);

        boolean enabled = PreferenceManager
                .getDefaultSharedPreferences(volume.getContext())
                .getInt(volume.getKey(), defaultValue) >= 0;

        volume.setCurrentValue(defaultValue);
        volume.setEnabled(enabled);

        return volume;
    }

    @NonNull
    private SwitchPreference getVibrateEnablePreference() {
        SwitchPreference enable = new SwitchPreference(getActivity());
        enable.setKey(SettingKeys.SoundProfile.VIBRATE + "_" + id);
        enable.setTitle("Vibrate");
        enable.setDefaultValue(false);
        return enable;
    }

}