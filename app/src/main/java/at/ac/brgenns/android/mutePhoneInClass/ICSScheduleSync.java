package at.ac.brgenns.android.mutePhoneInClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import at.ac.brgenns.android.mutePhoneInClass.prefs.SettingKeys;

/**
 * Created by Christoph on 02.01.2017.
 */

public class ICSScheduleSync extends AsyncTask<String, Void, Void> {
    private static final String TAG = ICSScheduleSync.class.getSimpleName();
    private Set<String> prefIDs;
    private SharedPreferences prefs;
    private Context context;

    public ICSScheduleSync(Context context) {
        this.context = context;
    }

    /**
     * A method to convert an InputStream into a String
     *
     * @param is An InputStream that is to be converted to a String
     * @return The string contained by the InputStream
     * @throws IOException
     */
    private static String streamToString(InputStream is) throws IOException {
        /*
         * Foreign code, source: http://www.kodejava.org/examples/266.html
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefIDs = prefs.getStringSet(SettingKeys.RULES_UIDS, new HashSet<String>());

        for (String id : prefIDs) {
            if (prefs.contains(SettingKeys.ICS.ICS_URL + "_" + id)) {
                try {
                    Calendar calendar =
                            getCalendar(prefs.getString(SettingKeys.ICS.ICS_URL + "_" + id, ""));

                    Collection<VEvent> eventsList = getFilteredvEvents(calendar);
                        if (!eventsList.isEmpty()) {
                            PriorityQueue<VEvent> events = getSortedvEvents(eventsList);

                            // Save calendar and next Event
                            SharedPreferences.Editor editor = prefs.edit();
                            VEvent nextEvent = events.peek();
                            editor.putLong(SettingKeys.ICS.NEXT_EVENT_START + "_" + id,
                                    nextEvent.getStartDate().getDate().getTime());
                            editor.putLong(SettingKeys.ICS.NEXT_EVENT_END + "_" + id,
                                    nextEvent.getEndDate().getDate().getTime());
                            editor.putString(SettingKeys.ICS.NEXT_EVENT_REASON + "_" + id,
                                    nextEvent.getSummary().getValue());
                            //save only future events
                            ComponentList<CalendarComponent> futureEvents = new ComponentList();
                            futureEvents.addAll(eventsList);
                            calendar = new Calendar(futureEvents);
                            editor.putString(SettingKeys.ICS.ICAL + "_" + id, calendar.toString());
                            editor.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    static Collection<VEvent> getFilteredvEvents(Calendar calendar) {
        Period period =
                new Period(new DateTime(
                        java.util.Calendar.getInstance().getTime()),
                        new Dur(26));
        PeriodRule[] rules = {new PeriodRule(period)};
        Filter filter = new Filter(rules, Filter.MATCH_ANY);
        return (Collection<VEvent>) filter.filter(calendar.getComponents(Component.VEVENT));
    }

    @NonNull
    static PriorityQueue<VEvent> getSortedvEvents(Collection<VEvent> eventsList) {

        PriorityQueue<VEvent> events = new PriorityQueue<>(200,
                new Comparator<VEvent>() {
                    @Override
                    public int compare(VEvent e1, VEvent e2) {
                        Date d1 = e1.getStartDate().getDate();
                        Date d2 = e2.getStartDate().getDate();
                        return d1.compareTo(d2);
                    }
                });
        for (VEvent event : eventsList) {
            events.add(
                    event); // with addAll the result seems not to be sorted
        }
        return events;
    }

    private Calendar getCalendar(String url) {

        try {
            URLConnection conn = new URL(url).openConnection();
//            conn.setDoOutput(true);
            InputStream response = conn.getInputStream();
            String ical = streamToString(response);
            StringReader sr = new StringReader(ical);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(sr);

            return calendar;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}