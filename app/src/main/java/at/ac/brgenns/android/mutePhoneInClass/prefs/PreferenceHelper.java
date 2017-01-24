package at.ac.brgenns.android.mutePhoneInClass.prefs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Christoph on 12.06.2016.
 */
public class PreferenceHelper {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.

                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (!(preference instanceof RingtonePreference)) {

                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    public static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static boolean addID(Activity activity, String id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> ids = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        boolean success = false;
        if (!ids.contains(id)) {
            Set<String> idsToStore = new HashSet<>(ids);
            idsToStore.add(id);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(SettingKeys.RULES_UIDS, idsToStore);
            success = editor.commit();
        }
        return success;
    }

    public static <T extends Enum<T>> void validateOrDeleteRule(Activity activity, String id,
                                                                Class<T> settingType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> ids = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        if (ids.contains(id)) {
            boolean valid = getRuleType(prefs, id) != SettingKeys.SettingType.UNDEFINED;
            if (!valid) {
                deleteRule(activity, id, settingType);
            }
        }
    }

    public static <T extends Enum<T>> boolean deleteRule(Activity activity, String id,
                                                         Class<T> settingType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> ids = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());
        boolean success = false;
        if (ids.contains(id)) {
            Set<String> idsToStore = new HashSet<>(ids);
            idsToStore.remove(id);
            SharedPreferences.Editor editor = prefs.edit();
            for (T key : EnumSet.allOf(settingType)) {
                String prefsKey = key.name() + "_" + id;
                if (prefs.contains(prefsKey)) {
                    editor.remove(prefsKey);
                }
            }
            editor.putStringSet(SettingKeys.RULES_UIDS, idsToStore);
            success = editor.commit();
        }
        return success;
    }

    public static SettingKeys.SettingType getRuleType(SharedPreferences prefs, String id) {
        if (!prefs.contains(SettingKeys.Kusss.USER + "_" + id) &&
                !prefs.contains(SettingKeys.WebUntis.SERVER_URL + "_" + id) &&
                !prefs.contains(SettingKeys.ICS.ICS_URL + "_" + id) &&
                prefs.contains(SettingKeys.Wifi.SSID + "_" + id)) {
            return SettingKeys.SettingType.WIFI;
        } else if (prefs.contains(SettingKeys.ICS.ICS_URL + "_" + id)) {
            return SettingKeys.SettingType.ICS;
        } else if (prefs.contains(SettingKeys.WebUntis.SERVER_URL + "_" + id)) {
            return SettingKeys.SettingType.WEBUNTIS;
        } else if (prefs.contains(SettingKeys.Kusss.USER + "_" + id)) {
            return SettingKeys.SettingType.KUSSS;
        } else if (prefs.contains(SettingKeys.SoundProfile.ALARM_VOLUME + "_" + id) &&
                prefs.contains(SettingKeys.SoundProfile.MEDIA_VOLUME + "_" + id) &&
                prefs.contains(SettingKeys.SoundProfile.RINGER_VOLUME + "_" + id) &&
                prefs.contains(SettingKeys.SoundProfile.VIBRATE + "_" + id)) {
            return SettingKeys.SettingType.SOUNDPROFILE;
        } else {
            return SettingKeys.SettingType.UNDEFINED;
        }

    }
}
