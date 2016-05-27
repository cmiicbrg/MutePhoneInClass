package at.ac.brgenns.android.mutePhoneInClass.prefs.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import at.ac.brgenns.android.mutePhoneInClass.R;

/**
 * Created by Christoph on 16.05.2016.
 */
public class MPICSQLiteHelper extends SQLiteOpenHelper {

    public static final String SOUND_PROFILE = "soundProfile";
    public static final String SOUND_PROFILE_ID = "soundProfileID";
    public static final String SOUND_PROFILE_NAME = "name";
    public static final String SOUND_PROFILE_MEDIA_VOLUME = "mediaVolume";
    public static final String SOUND_PROFILE_ALARM_VOLUME = "alarmVolume";
    public static final String SOUND_PROFILE_RING_VOLUME = "ringVolume";
    public static final String SOUND_PROFILE_VIBRATE = "vibrate";
    public static final String WIFI_EVENT = "wifiEvent";
    public static final String WIFI_EVENT_ID = "wifiEventID";
    public static final String WIFI_EVENT_ACTIVE = "active";
    public static final String WIFI_EVENT_SSID = "SSID";
    public static final String WIFI_EVENT_DAYS = "days";
    public static final String WIFI_EVENT_STARTTIME = "starttime";
    public static final String WIFI_EVENT_ENDTIME = "endtime";
    public static final String WIFI_EVENT_SOUND_PROFILE = SOUND_PROFILE + "_" + SOUND_PROFILE_ID;
    public static final String EVENT_PROVIDER = "eventProvider";
    public static final String EVENT_PROVIDER_ID = "eventProviderID";
    public static final String EVENT_PROVIDER_ACTIVE = "active";
    public static final String EVENT_PROVIDER_TYPE = "type";
    public static final String EVENT_PROVIDER_URL = "url";
    public static final String EVENT_PROVIDER_CLASS_NAME = "className";
    public static final String EVENT_PROVIDER_SOUND_PROFILE =
            SOUND_PROFILE + "_" + SOUND_PROFILE_ID;
    public static final String EVENT = "event";
    public static final String EVENT_ID = "eventID";
    public static final String EVENT_BEGIN = "begin";
    public static final String EVENT_END = "end";
    public static final String EVENT_EVENT_PROVIDER = EVENT_PROVIDER + "_" + EVENT_PROVIDER_ID;
    private static final String DATABASE_NAME = "mpic.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SOUND_CREATE = "CREATE TABLE " + SOUND_PROFILE + " (\n" +
            "  " + SOUND_PROFILE_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE ,\n" +
            "  " + SOUND_PROFILE_NAME + " VARCHAR NOT NULL ,\n" +
            "  " + SOUND_PROFILE_MEDIA_VOLUME + " INTEGER NOT NULL ,\n" +
            "  " + SOUND_PROFILE_ALARM_VOLUME + " INTEGER NOT NULL,\n" +
            "  " + SOUND_PROFILE_RING_VOLUME + " INTEGER NOT NULL ,\n" +
            "  " + SOUND_PROFILE_VIBRATE + " INTEGER NOT NULL \n" +
            ");\n" +
            "\n";
    private static final String WIFI_CREATE = "CREATE TABLE " + WIFI_EVENT + " (\n" +
            "  " + WIFI_EVENT_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE ,\n" +
            "  " + WIFI_EVENT_ACTIVE + " INTEGER NOT NULL ,\n" +
            "  " + WIFI_EVENT_SSID + " VARCHAR NOT NULL ,\n" +
            "  " + WIFI_EVENT_DAYS + " VARCHAR NULL ,\n" +
            "  " + WIFI_EVENT_STARTTIME + " VARCHAR NULL ,\n" +
            "  " + WIFI_EVENT_ENDTIME + " VARCHAR NULL ,\n" +
            "  " + WIFI_EVENT_SOUND_PROFILE + " INTEGER NOT NULL ,\n" +
            "  FOREIGN KEY (" + WIFI_EVENT_SOUND_PROFILE + ") REFERENCES " + SOUND_PROFILE + " (" +
            SOUND_PROFILE_ID + ")\n" +
            ");\n" +
            "\n";
    private static final String EVENT_PROVIDER_CREATE = "CREATE TABLE " + EVENT_PROVIDER + " (\n" +
            "  " + EVENT_PROVIDER_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE ,\n" +
            "  " + EVENT_PROVIDER_ACTIVE + " INTEGER NOT NULL ,\n" +
            "  " + EVENT_PROVIDER_TYPE + " VARCHAR NOT NULL ,\n" +
            "  " + EVENT_PROVIDER_URL + " TEXT NOT NULL , \n" +
            "  " + EVENT_PROVIDER_CLASS_NAME + " VARCHAR ,\n" +
            "  " + EVENT_PROVIDER_SOUND_PROFILE + " INTEGER NOT NULL ,\n" +
            "  FOREIGN KEY (" + EVENT_PROVIDER_SOUND_PROFILE + ") REFERENCES " + SOUND_PROFILE +
            " (" + SOUND_PROFILE_ID + ")\n" +
            ");\n" +
            "\n";
    private static final String EVENT_CREATE = "CREATE TABLE " + EVENT + " (\n" +
            "  " + EVENT_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE ,\n" +
            "  " + EVENT_BEGIN + " VARCHAR NOT NULL ,\n" +
            "  " + EVENT_END + " VARCHAR NOT NULL ,\n" +
            "  " + EVENT_EVENT_PROVIDER + " INTEGER NOT NULL ,\n" +
            "  FOREIGN KEY (" + EVENT_EVENT_PROVIDER + ") REFERENCES " + EVENT_PROVIDER + "(" +
            EVENT_PROVIDER_ID + ")\n" +
            ");\n" +
            "\n";

    private Context context;

    public MPICSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SOUND_CREATE);
        db.execSQL(WIFI_CREATE);
        db.execSQL(EVENT_PROVIDER_CREATE);
        db.execSQL(EVENT_CREATE);
        ContentValues values = new ContentValues();
        values.put(SOUND_PROFILE_ID, 0);
        values.put(SOUND_PROFILE_NAME, context.getString(R.string.alarms_only));
        values.put(SOUND_PROFILE_MEDIA_VOLUME, 0);
        values.put(SOUND_PROFILE_ALARM_VOLUME, -1);
        values.put(SOUND_PROFILE_RING_VOLUME, 0);
        values.put(SOUND_PROFILE_VIBRATE, 0);
        db.insert(SOUND_PROFILE, null, values);
        values = new ContentValues();
        values.put(SOUND_PROFILE_ID, 1);
        values.put(SOUND_PROFILE_NAME, context.getString(R.string.total_silence));
        values.put(SOUND_PROFILE_MEDIA_VOLUME, 0);
        values.put(SOUND_PROFILE_ALARM_VOLUME, 0);
        values.put(SOUND_PROFILE_RING_VOLUME, 0);
        values.put(SOUND_PROFILE_VIBRATE, 0);
        db.insert(SOUND_PROFILE, null, values);
//        values = new ContentValues();
//        values.put(WIFI_EVENT_ACTIVE, 1);
//        values.put(WIFI_EVENT_SSID, "ChriMarg");
//        values.put(WIFI_EVENT_DAYS, "");
//        values.put(WIFI_EVENT_STARTTIME, "");
//        values.put(WIFI_EVENT_ENDTIME, "");
//        values.put(WIFI_EVENT_SOUND_PROFILE, 0);
//        db.insert(WIFI_EVENT, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MPICSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_PROVIDER);
        db.execSQL("DROP TABLE IF EXISTS " + WIFI_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + SOUND_PROFILE);
        onCreate(db);
    }
}
