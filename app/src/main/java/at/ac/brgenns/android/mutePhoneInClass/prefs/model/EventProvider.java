package at.ac.brgenns.android.mutePhoneInClass.prefs.model;

/**
 * Created by Christoph on 17.05.2016.
 */
public class EventProvider {
    private long eventProiderID;
    private boolean active;
    private String type;
    private String url;
    private String className;
    private SoundProfile soundProfile;

    public long getEventProviderID() {
        return eventProiderID;
    }

    public void setEventProiderID(long eventProiderID) {
        this.eventProiderID = eventProiderID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public SoundProfile getSoundProfile() {
        return soundProfile;
    }

    public void setSoundProfile(SoundProfile soundProfile) {
        this.soundProfile = soundProfile;
    }
}
