package at.ac.brgenns.android.mutePhoneInClass.prefs;

/**
 * Created by Christoph on 15.06.2016.
 */
public class SettingKeys {

    public static final String MUTE_ENABLED = "mute_enabled";
    public static final String RULES_UIDS = "rule_uids";

    public static final String LAST_MEDIA_VOLUME = "last_media_volume";
    public static final String LAST_ALARM_VOLUME = "last_alarm_volume";
    public static final String LAST_RINGER_VOLUME = "last_ringer_volume";
    public static final String LAST_RINGER_MODE = "last_vibrate_state";
    public static final String DISABLED_FOR = "disabled_for";

    public enum SettingType {
        WIFI,
        SOUNDPROFILE,
        KUSSS,
        ICS,
        WEBUNTIS,
        UNDEFINED
    }

    public enum Wifi {
        ENABLE,
        RULE_NAME,
        SSID,
        SOUND_PROFILE
    }

    public enum SoundProfile {
        RULE_NAME,
        ALARM_VOLUME,
        MEDIA_VOLUME,
        RINGER_VOLUME,
        VIBRATE
    }

    public enum Kusss {
        ENABLE,
        USER,
        LAST_SYNC,
        SSID,
        SOUND_PROFILE,
        NEXT_EVENT_START,
        NEXT_EVENT_END,
        NEXT_EVENT_REASON,
        ICAL
    }
    public enum ICS {
        ENABLE,
        ICS_URL,
        LAST_SYNC,
        SSID,
        SOUND_PROFILE,
        NEXT_EVENT_START,
        NEXT_EVENT_END,
        NEXT_EVENT_REASON,
        ICAL
    }
    public enum WebUntis {
        ENABLE,
        SERVER_URL,
        SCHOOL_NAME,
        CLASS_NAME,
        USER,
        LAST_SYNC,
        SSID,
        SOUND_PROFILE,
        NEXT_EVENT_START,
        NEXT_EVENT_END,
        NEXT_EVENT_REASON,
        ICAL
    }

    public enum GenericSchedule {
        SSID,
        SOUND_PROFILE,
        LAST_SYNC,
        NEXT_EVENT_START,
        NEXT_EVENT_END,
        NEXT_EVENT_REASON,
        ICAL

    }
}
