package at.ac.brgenns.android.mutePhoneInClass.prefs.model;

/**
 * Created by Christoph on 16.05.2016.
 */
public class SoundProfile {
    private long soundProfileID = -1;
    private String name;
    private int mediaVolume;
    private int alarmVolume;
    private int ringVolume;
    private boolean vibrate;

    public SoundProfile(String name, int mediaVolume, int alarmVolume, int ringVolume,
                        boolean vibrate) {
        setName(name);
        setMediaVolume(mediaVolume);
        setAlarmVolume(alarmVolume);
        setRingVolume(ringVolume);
        setVibrate(vibrate);
    }

    public SoundProfile() {

    }

    public long getSoundProfileID() {
        return soundProfileID;
    }

    public void setSoundProfileID(long soundProfileID) {
        this.soundProfileID = soundProfileID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMediaVolume() {
        return mediaVolume;
    }

    public void setMediaVolume(int mediaVolume) {
        this.mediaVolume = mediaVolume;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public void setAlarmVolume(int alarmVolume) {
        this.alarmVolume = alarmVolume;
    }

    public int getRingVolume() {
        return ringVolume;
    }

    public void setRingVolume(int ringVolume) {
        this.ringVolume = ringVolume;
    }

    public boolean getVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean hasId() {
        return getSoundProfileID() >= 0;
    }
}
