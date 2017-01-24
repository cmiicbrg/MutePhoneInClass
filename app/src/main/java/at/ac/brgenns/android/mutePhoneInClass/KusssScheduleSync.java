package at.ac.brgenns.android.mutePhoneInClass;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import at.ac.brgenns.android.mutePhoneInClass.prefs.SettingKeys;

import static at.ac.brgenns.android.mutePhoneInClass.ICSScheduleSync.getFilteredvEvents;
import static at.ac.brgenns.android.mutePhoneInClass.ICSScheduleSync.getSortedvEvents;

/**
 * Created by Christoph on 02.01.2017.
 */

public class KusssScheduleSync extends AsyncTask<String, Void, Void> {
    private static final String TAG = KusssScheduleSync.class.getSimpleName();
    String userName = "";
    CookieManager cookieMan;
    private Set<String> prefIDs;
    private SharedPreferences prefs;
    private Context context;

    public KusssScheduleSync(Context context) {
        this.context = context;
        cookieMan = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
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
            if (prefs.contains(SettingKeys.Kusss.USER + "_" + id)) {
                userName =
                        prefs.getString(SettingKeys.Kusss.USER + "_" + id, "");
                if (!userName.isEmpty()) {
                    final String finalUserName = userName;
                    Account account = new Account(finalUserName,
                            AccountAuthenticatorService.AUTH_TYPE);
                    AccountManager accountManager =
                            AccountManager.get(context);
                    String password = accountManager.getPassword(account);

                    if (password != null) {
                        Log.d(TAG, "password retrieved" + password.length());
                        if (password.length() > 0) {
                            try {
                                Calendar calendar = getCalendar(finalUserName, password);
                                Collection<VEvent> eventsList = getFilteredvEvents(calendar);
                                if (!eventsList.isEmpty()) {
                                    PriorityQueue<VEvent> events = getSortedvEvents(eventsList);

                                    // Save calendar and next Event
                                    SharedPreferences.Editor editor = prefs.edit();
                                    VEvent nextEvent = events.peek();
                                    editor.putLong(SettingKeys.Kusss.NEXT_EVENT_START + "_" + id,
                                            nextEvent.getStartDate().getDate().getTime());
                                    editor.putLong(SettingKeys.Kusss.NEXT_EVENT_END + "_" + id,
                                            nextEvent.getEndDate().getDate().getTime());
                                    editor.putString(SettingKeys.Kusss.NEXT_EVENT_REASON + "_" + id,
                                            nextEvent.getSummary().getValue());
                                    //save only future events
                                    ComponentList<CalendarComponent> futureEvents = new ComponentList();
                                    futureEvents.addAll(eventsList);
                                    calendar = new Calendar(futureEvents);
                                    editor.putString(SettingKeys.Kusss.ICAL + "_" + id, calendar.toString());
                                    editor.commit();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void login(String uid, String pwd) throws Exception {
        String data = URLEncoder.encode("j_username", "UTF-8") + "=" +
                URLEncoder.encode(uid, "UTF-8");
        data += "&" + URLEncoder.encode("j_password", "UTF-8") + "=" +
                URLEncoder.encode(pwd, "UTF-8");
        data += "&" + URLEncoder.encode("submit", "UTF-8") + "=" +
                URLEncoder.encode("login", "UTF-8");

        CookieHandler.setDefault(cookieMan);

        URL url = new URL("https://www.kusss.jku.at/kusss/login.action");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        InputStream response = conn.getInputStream();

        String html = streamToString(response);

        if (html.contains("<span class=\"hideme\">Angemeldet als</span>")) {
            return;
        } else {
            throw new Exception(
                    "Could not verify credentials, username and/or password are wrong");
        }
    }

    private void logout() {
        try {
            CookieHandler.setDefault(cookieMan);
            new URL("https://www.kusss.jku.at/kusss/logout.action").openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Calendar getCalendar(String finalUserName, String password) {

        try {
            login(finalUserName, password);
            String data = URLEncoder.encode("selectAll", "UTF-8") + "=" +
                    URLEncoder.encode("ical.category.mycourses", "UTF-8") + "&" +
                    URLEncoder.encode("selectAll", "UTF-8") + "=" +
                    URLEncoder.encode("ical.category.examregs", "UTF-8");

            CookieHandler.setDefault(cookieMan);
            String url = "https://www.kusss.jku.at/kusss/ical-multi-sz.action";
            URLConnection conn = new URL(url).openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            InputStream response = conn.getInputStream();
            String ical = streamToString(response);
            StringReader sr = new StringReader(ical);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(sr);

            logout();
            return calendar;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logout();
        }
        return null;
    }
}