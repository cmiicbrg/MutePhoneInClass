package at.ac.brgenns.android.mutePhoneInClass;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import at.ac.brgenns.android.mutePhoneInClass.prefs.PreferenceHelper;
import at.ac.brgenns.android.mutePhoneInClass.prefs.SettingKeys;

import static at.ac.brgenns.android.mutePhoneInClass.ICSScheduleSync.getFilteredvEvents;
import static at.ac.brgenns.android.mutePhoneInClass.ICSScheduleSync.getSortedvEvents;

/**
 * Created by Christoph on 02.01.2017.
 */

public class WebUntisScheduleSync extends AsyncTask<String, Void, Void> {
    private static final String TAG = WebUntisScheduleSync.class.getSimpleName();
    String userName = "";
    private Set<String> prefIDs;
    private SharedPreferences prefs;
    private Context context;
    private JSONRPC2Session mySession;

    public WebUntisScheduleSync(Context context) {
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
            if (PreferenceHelper.getRuleType(prefs,id) == SettingKeys.SettingType.WEBUNTIS) {
                userName =
                        prefs.getString(SettingKeys.WebUntis.USER + "_" + id, "");
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
                                Calendar calendar = getCalendar(finalUserName, password, id);

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
                                    ComponentList<CalendarComponent> futureEvents =
                                            new ComponentList();
                                    futureEvents.addAll(eventsList);
                                    calendar = new Calendar(futureEvents);
                                    editor.putString(SettingKeys.ICS.ICAL + "_" + id,
                                            calendar.toString());
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

    private JSONRPC2Response login(String uid, String pwd, String id) throws Exception {
        String rid = "req-001";
        String method = "authenticate";
        Map<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("user", uid);
        requestParams.put("password", pwd);
        requestParams.put("client", "TestApp");

        JSONRPC2Request request = new JSONRPC2Request(method, requestParams, rid);
        try {
            String url = prefs.getString(SettingKeys.WebUntis.SERVER_URL + "_" + id, "");
            String schoolName = prefs.getString(SettingKeys.WebUntis.SCHOOL_NAME + "_" + id, "");
            URL serverURL =
                    new URL("https://" + url + "/WebUntis/jsonrpc.do?school=" + schoolName);
            mySession = new JSONRPC2Session(serverURL);
            mySession.getOptions().acceptCookies(true);

            JSONRPC2Response response = mySession.send(request);
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONRPC2SessionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Calendar getCalendar(String finalUserName, String password, String id) {

        Calendar calendar = new Calendar();

        try {
            JSONRPC2Response login = login(finalUserName, password, id);
            if (login != null && login.indicatesSuccess()) {

                JSONObject resp = new JSONObject(login.getResult().toString());
                Log.d("response", login.getID().toString());

                String rid = "req-002";
                String method = "getTimetable";

                String personID = resp.getString("personId");
                Log.d("personID", personID);

                String personType = resp.getString("personType");
                Log.d("personType", personType);

                HashMap<String, Object> requestParams = new HashMap<String, Object>();

                buildRequestParams(personID, personType, requestParams);

                JSONRPC2Request request = new JSONRPC2Request(method, requestParams, rid);
                Log.d("request", request.toString());

                JSONRPC2Response response = mySession.send(request);
                Log.d("response", response.getResult().toString());

                JSONArray arr = new JSONArray(response.getResult().toString());
                Log.d("Webuntis", arr.toString(4));

                jsonToCalendar(arr, calendar);
            }
            return calendar;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JSONRPC2SessionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void jsonToCalendar(JSONArray arr, Calendar calendar) {
        DateTime eventStart;
        DateTime eventEnd;
        String subject;
        for (int i = 0; i < arr.length(); i++) {
            try {
                subject = "";
                JSONObject event = (JSONObject) arr.get(i);
                String date = event.getString("date");
                String startTime = event.getString("startTime");
                if (startTime.length() < 4) {
                    startTime = "0" + startTime;
                }
                eventStart = new DateTime(
                        new SimpleDateFormat("yyyyMMddHHmm").parse(date + startTime).getTime());
                String endTime = event.getString("endTime");
                if (endTime.length() < 4) {
                    endTime = "0" + endTime;
                }
                eventEnd = new DateTime(
                        new SimpleDateFormat("yyyyMMddHHmm").parse(date + endTime).getTime());

                JSONArray classArr = event.getJSONArray("kl");
                for (int j = 0; j < classArr.length(); j++) {
                    JSONObject classes = (JSONObject) classArr.get(j);
                    subject += classes.getString("name");
                    if (j < classArr.length() - 1) {
                        subject += ", ";
                    }
                }
                JSONArray subjectArr = event.getJSONArray("su");
                if (!subject.isEmpty() && subjectArr.length() > 0) {
                    subject += " - ";
                }
                for (int j = 0; j < subjectArr.length(); j++) {
                    JSONObject subjects = (JSONObject) subjectArr.get(j);
                    subject += subjects.getString("name");
                    if (j < subjectArr.length() - 1) {
                        subject += ", ";
                    }
                }

                VEvent vEvent = new VEvent();

                calendar.getComponents().add(new VEvent(eventStart, eventEnd, subject));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildRequestParams(String personID, String personType,
                                    HashMap<String, Object> requestParams) {
        HashMap<String, Object> options = new HashMap<String, Object>();
        HashMap<String, Object> element = new HashMap<String, Object>();
        element.put("id", personID);
        element.put("type", personType);
        options.put("startDate", new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
        options.put("endDate", new SimpleDateFormat("yyyyMMdd").format(new java.util.Date(
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7))));
        options.put("element", element);
        options.put("klasseFields", new String[]{"id", "name"});
        options.put("subjectFields", new String[]{"id", "name"});
        requestParams.put("options", options);
    }
}