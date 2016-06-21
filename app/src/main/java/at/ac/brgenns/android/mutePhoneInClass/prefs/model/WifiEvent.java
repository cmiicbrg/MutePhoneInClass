package at.ac.brgenns.android.mutePhoneInClass.prefs.model;

import java.util.Date;

/**
 * Created by Christoph on 16.05.2016.
 */
public class WifiEvent {
    private long wifiID = -1;
    private boolean active;
    private String SSID;
    private String days;
    private Date starttime;
    private Date endtime;
    private SoundProfile soundProfile;

    public WifiEvent(boolean active, String ssid, String days,
                     Date starttime, Date endtime,
                     SoundProfile soundProfile) {
        setActive(active);
        setSSID(ssid);
        setDays(days);
        setStarttime(starttime);
        setEndtime(endtime);
        setSoundProfile(soundProfile);
    }

    public WifiEvent() {

    }

    public long getWifiID() {
        return wifiID;
    }

    public void setWifiID(long wifiID) {
        this.wifiID = wifiID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public SoundProfile getSoundProfile() {
        return soundProfile;
    }

    public void setSoundProfile(SoundProfile soundProfile) {
        this.soundProfile = soundProfile;
    }

    public boolean hasId() {
        return wifiID > -1;
    }
}
