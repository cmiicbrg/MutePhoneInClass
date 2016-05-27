package at.ac.brgenns.android.mutePhoneInClass.prefs.model;

import java.util.Date;

/**
 * Created by Christoph on 17.05.2016.
 */
public class Event {
    private long eventID;
    private Date begin;
    private Date end;
    private EventProvider eventProvider;

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public EventProvider getEventProvider() {
        return eventProvider;
    }

    public void setEventProvider(EventProvider eventProvider) {
        this.eventProvider = eventProvider;
    }
}
