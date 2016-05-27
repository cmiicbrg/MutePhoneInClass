package at.ac.brgenns.android.mutePhoneInClass.prefs.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

import at.ac.brgenns.android.mutePhoneInClass.prefs.model.Event;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.EventProvider;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.SoundProfile;
import at.ac.brgenns.android.mutePhoneInClass.prefs.model.WifiEvent;

import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_BEGIN;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_END;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_EVENT_PROVIDER;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_ID;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_PROVIDER_ACTIVE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_PROVIDER_CLASS_NAME;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_PROVIDER_ID;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_PROVIDER_SOUND_PROFILE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_PROVIDER_TYPE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.EVENT_PROVIDER_URL;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE_ALARM_VOLUME;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE_ID;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE_MEDIA_VOLUME;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE_NAME;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE_RING_VOLUME;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.SOUND_PROFILE_VIBRATE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_ACTIVE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_DAYS;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_ENDTIME;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_ID;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_SOUND_PROFILE;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_SSID;
import static at.ac.brgenns.android.mutePhoneInClass.prefs.db.MPICSQLiteHelper.WIFI_EVENT_STARTTIME;

/**
 * Created by Christoph on 17.05.2016.
 */
public class PreferenceDataSource {
    private SQLiteDatabase database;
    private MPICSQLiteHelper dbHelper;
    private String[] eventColumns = {
            EVENT_ID,
            EVENT_BEGIN,
            EVENT_END,
            EVENT_EVENT_PROVIDER
    };
    private String[] eventProviderColumns = {
            EVENT_PROVIDER_ID,
            EVENT_PROVIDER_ACTIVE,
            EVENT_PROVIDER_TYPE,
            EVENT_PROVIDER_URL,
            EVENT_PROVIDER_CLASS_NAME,
            EVENT_PROVIDER_SOUND_PROFILE
    };
    private String[] soundProfileColumns = {
            SOUND_PROFILE_ID,
            SOUND_PROFILE_NAME,
            SOUND_PROFILE_MEDIA_VOLUME,
            SOUND_PROFILE_ALARM_VOLUME,
            SOUND_PROFILE_RING_VOLUME,
            SOUND_PROFILE_VIBRATE
    };
    private String[] wifiEventColumns = {
            WIFI_EVENT_ID,
            WIFI_EVENT_ACTIVE,
            WIFI_EVENT_SSID,
            WIFI_EVENT_DAYS,
            WIFI_EVENT_STARTTIME,
            WIFI_EVENT_ENDTIME,
            WIFI_EVENT_SOUND_PROFILE
    };

    public PreferenceDataSource(Context context) {
        dbHelper = new MPICSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public SoundProfile createSoundProfile(SoundProfile soundProfile) {
        if (!soundProfile.hasId()) {
            ContentValues values = getContentValues(soundProfile);
            soundProfile.setSoundProfileID(database.insert(SOUND_PROFILE, null, values));
        }
        return soundProfile;
    }

    public SoundProfile createSoundProfile(String name, int mediaVolume, int alarmVolume,
                                           int ringVolume, boolean vibrate) {
        SoundProfile soundProfile =
                new SoundProfile(name, mediaVolume, alarmVolume, ringVolume, vibrate);
        return createSoundProfile(soundProfile);
    }

    public TreeMap<Long, SoundProfile> getAllSoundProfiles() {
        TreeMap<Long, SoundProfile> soundProfiles = new TreeMap();

        SQLiteCursor cursor = (SQLiteCursor) database
                .query(SOUND_PROFILE, soundProfileColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SoundProfile soundProfile = getSoundProfile(cursor);
            soundProfiles.put(soundProfile.getSoundProfileID(), soundProfile);
            cursor.moveToNext();
        }

        cursor.close();
        return soundProfiles;
    }

    public WifiEvent createWifiEvent(WifiEvent wifiEvent) {
        if (!wifiEvent.hasId()) {
            ContentValues values = getContentValues(wifiEvent);
            wifiEvent.setWifiID(database.insert(WIFI_EVENT, null, values));
        }
        return wifiEvent;
    }

    public WifiEvent createWifiEvent(boolean active, String SSID, String days,
                                        Date starttime, Date endtime, SoundProfile soundProfile) {
        WifiEvent wifiEvent =
                new WifiEvent(active, SSID, days,
                        starttime, endtime, soundProfile);
        return createWifiEvent(wifiEvent);
    }

    public TreeMap<Long, WifiEvent> getAllWifiEvents() {
        TreeMap<Long, WifiEvent> wifiEvents = new TreeMap();

        SQLiteCursor cursor = (SQLiteCursor) database
                .query(WIFI_EVENT, wifiEventColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WifiEvent wifiEvent = getWifiEvent(cursor);
            wifiEvents.put(wifiEvent.getWifiID(), wifiEvent);
            cursor.moveToNext();
        }

        cursor.close();
        return wifiEvents;
    }

    private WifiEvent getWifiEvent(SQLiteCursor cursor) {
        WifiEvent wifiEvent = new WifiEvent();
        wifiEvent.setWifiID(cursor.getLong(cursor.getColumnIndex(WIFI_EVENT_ID)));
        wifiEvent.setActive(
                cursor.getInt(cursor.getColumnIndex(WIFI_EVENT_ACTIVE)) == 1 ? true : false);
        wifiEvent.setSSID(cursor.getString(cursor.getColumnIndex(WIFI_EVENT_SSID)));
        wifiEvent.setDays(cursor.getString(cursor.getColumnIndex(WIFI_EVENT_DAYS)));
        wifiEvent.setStarttime(
                fromTimeString(cursor.getString(cursor.getColumnIndex(WIFI_EVENT_STARTTIME))));
        wifiEvent.setEndtime(
                fromTimeString(cursor.getString(cursor.getColumnIndex(WIFI_EVENT_ENDTIME))));
        //TODO: write method for retrieving a single Soundprofile
        wifiEvent.setSoundProfile(getAllSoundProfiles()
                .get(cursor.getInt(cursor.getColumnIndex(WIFI_EVENT_SOUND_PROFILE))));
        return wifiEvent;
    }

    public TreeMap<Long, EventProvider> getAllEventProviders() {

        return new TreeMap<>();
    }

    @NonNull
    private SoundProfile getSoundProfile(Cursor cursor) {
        SoundProfile soundProfile = new SoundProfile();
        soundProfile.setSoundProfileID(cursor.getLong(cursor.getColumnIndex(SOUND_PROFILE_ID)));
        soundProfile.setName(cursor.getString(cursor.getColumnIndex(SOUND_PROFILE_NAME)));
        soundProfile
                .setMediaVolume(cursor.getInt(cursor.getColumnIndex(SOUND_PROFILE_MEDIA_VOLUME)));
        soundProfile
                .setMediaVolume(cursor.getInt(cursor.getColumnIndex(SOUND_PROFILE_ALARM_VOLUME)));
        soundProfile.setRingVolume(cursor.getInt(cursor.getColumnIndex(SOUND_PROFILE_RING_VOLUME)));
        soundProfile.setVibrate(cursor.getInt(cursor.getColumnIndex(SOUND_PROFILE_VIBRATE)) > 0);
        return soundProfile;
    }

    private int updateSoundProfile(SoundProfile soundProfile) {
        return database
                .update(SOUND_PROFILE, getContentValues(soundProfile), SOUND_PROFILE_ID + " = ?",
                        new String[]{String.valueOf(soundProfile.getSoundProfileID())});
    }

    @NonNull
    private ContentValues getContentValues(SoundProfile soundProfile) {
        ContentValues values = new ContentValues();
        values.put(SOUND_PROFILE_NAME, soundProfile.getName());
        values.put(SOUND_PROFILE_MEDIA_VOLUME, soundProfile.getMediaVolume());
        values.put(SOUND_PROFILE_ALARM_VOLUME, soundProfile.getAlarmVolume());
        values.put(SOUND_PROFILE_RING_VOLUME, soundProfile.getRingVolume());
        values.put(SOUND_PROFILE_VIBRATE, soundProfile.getVibrate());
        return values;
    }

    @NonNull
    private ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EVENT_BEGIN, toDateString(event.getBegin()));
        values.put(EVENT_END, toDateString(event.getEnd()));
        values.put(EVENT_EVENT_PROVIDER, event.getEventProvider().getEventProviderID());
        return values;
    }

    @NonNull
    private ContentValues getContentValues(EventProvider eventProvider) {
        ContentValues values = new ContentValues();
        values.put(EVENT_PROVIDER_ACTIVE, eventProvider.isActive());
        values.put(EVENT_PROVIDER_TYPE, eventProvider.getType());
        values.put(EVENT_PROVIDER_URL, eventProvider.getUrl());
        values.put(EVENT_PROVIDER_CLASS_NAME, eventProvider.getClassName());
        values.put(EVENT_PROVIDER_SOUND_PROFILE,
                eventProvider.getSoundProfile().getSoundProfileID());
        return values;
    }

    @NonNull
    private ContentValues getContentValues(WifiEvent wifiEvent) {
        ContentValues values = new ContentValues();
        values.put(WIFI_EVENT_ACTIVE, wifiEvent.isActive());
        values.put(WIFI_EVENT_SSID, wifiEvent.getSSID());
        values.put(WIFI_EVENT_DAYS, wifiEvent.getDays());
        values.put(WIFI_EVENT_STARTTIME, toTimeString(wifiEvent.getStarttime()));
        values.put(WIFI_EVENT_ENDTIME, toTimeString(wifiEvent.getEndtime()));
        values.put(WIFI_EVENT_SOUND_PROFILE, wifiEvent.getSoundProfile().getSoundProfileID());
        return values;
    }

    /**
     * get datetime
     */
    private String toDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date fromDateString(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    /**
     * get datetime
     */
    private String toTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date fromTimeString(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

}
