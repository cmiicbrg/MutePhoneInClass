package at.dAuzinger.kusssApi;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Main KUSSS API class, if you want to access the KUSSS through the API you should probably start here
 *
 * @author David Auzinger
 * @version 1.0
 */
public class KUSSS {
    KUSSSHandler handler;
    Logger logger;

    /**
     * Default constructor
     *
     * @param uid User id, e.g. k1234567
     * @param pwd User password
     */
    public KUSSS(String uid, String pwd) {
        logger = new Logger();
        logger.info("Initating API");
        handler = new KUSSSHandler(uid, pwd, logger);
    }

    /**
     * Gets all LVAs for a single semester
     *
     * @param semester The semester the LVAs are returned for
     * @return A list of LVAs the student participates in the given semester
     * @throws Exception Passed through exceptions
     */
    public LVA[] getLVAs(Semester semester) throws Exception {
        logger.info("Commencing getLVAs");

        try {
            handler.login();

            switchSemester(handler, semester);
            String source =
                    handler.getHTML("https://www.kusss.jku.at/kusss/listmystudentlvas.action");

            ArrayList<String> linkList = new ArrayList<String>();

            while (source.indexOf("<a href=\"sz-lvadetail-overview.action?courseId=") != -1) {
                source = source.substring(
                        source.indexOf("<a href=\"sz-lvadetail-overview.action?courseId=") + 9);
                linkList.add("https://www.kusss.jku.at/kusss/" +
                        source.substring(0, source.indexOf("\"")));
            }

            String[] links = linkList.toArray(new String[linkList.size()]);

            LVA[] lvas = new LVA[links.length];

            for (int i = 0; i < links.length; i++) {
                lvas[i] = new LVA(links[i], handler);
            }

            handler.logout();

            return lvas;
        } catch (Exception e) {
            logger.error("getLVAs: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get whole TimeTable for the current semester.
     *
     * @param sem The semester you want to get the TimeTable for
     * @return The TimeTable
     * @throws Exception Passed through errors
     */
    public TimeTable getTimeTable(Semester sem) throws Exception {
        logger.info("Commencing getTimeTable");

        try {
            handler.login();

            KUSSS.switchSemester(handler, sem);

            String[][] params = new String[1][2];

            //TODO

            params[0][0] = "selectAll";
            params[0][1] = "ical.category.mycourses";
            //params[1][0] = "selectAll";
            //params[1][1] = "ical.category.examregs";

            String ical = handler.getHTML(
                    "https://www.kusss.jku.at/kusss/ical-multi-sz.action?openIdical.category.mycourses=true",
                    params);

            handler.logout();

            return new TimeTable(ical, handler);
        } catch (Exception e) {
            logger.error("getTimeTable: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get the logger for the current KUSSS object
     *
     * @return The logger object
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Static method to switch the semester for a KUSSSHandler object. The Semester cookie will be saved in the handler object.
     *
     * @param kusssHandler The handler object which you want to switch the semester
     * @param sem          The semester you want to switch to.
     * @throws Exception Passed through exceptions
     */
    public static void switchSemester(KUSSSHandler kusssHandler, Semester sem) throws Exception {
        kusssHandler.logger.info("Commencing switchSemester");

        try {
            kusssHandler.getHTML("https://www.kusss.jku.at/kusss/select-term.action",
                    new String[][]{{"term", sem.getShort()}});
        } catch (FileNotFoundException e) {
            kusssHandler.logger.info("Ignore above warning");
            //An exception thrown because of the KUSSS redirecting the client to a nonexistent url, it can be ignored
        } catch (Exception e) {
            //Any other exception that should be handled specifically
            kusssHandler.logger.error("switchSemester" + e.getMessage());
            throw e;
        }
    }

    /**
     * Gets a list of all grades available
     *
     * @return List of all grades available
     * @throws Exception Passes through occuring Exceptions
     */
    public Grade[] getGrades() throws Exception {
        logger.info("Commencing getGrades");

        try {
            handler.login();

            String source = handler.getHTML("https://www.kusss.jku.at/kusss/notenauskunft.action",
                    new String[][]{{"months", "0"}});

            source = source.substring(source.indexOf("<th valign=\"top\">Datum</th>"));

            ArrayList<Grade> grades = new ArrayList<Grade>();

            while (source.indexOf("class=\"darkcell\">") != -1) {
                String dateStr, ectsStr, swstStr, title;

                source = source.substring(source.indexOf("class=\"darkcell\"><td>") + 21);
                dateStr = source.substring(0, 10);
                title = source.substring(source.indexOf("<strong>") + 8,
                        source.indexOf("</strong>")).trim();
                source = source.substring(source.indexOf("</acronym></td><td>") + 19);
                ectsStr = source.substring(0, 4);
                swstStr = source.substring(7, 11);
                DateTime date = new DateTime(Integer.parseInt(dateStr.substring(6)),
                        Integer.parseInt(dateStr.substring(3, 5)),
                        Integer.parseInt(dateStr.substring(0, 2)));
                float ects = Float.parseFloat(ectsStr.replace(',', '.'));
                float swst = Float.parseFloat(swstStr.replace(',', '.'));

                int semPos, nrPos;
                semPos = title.length() - 6;
                nrPos = semPos - 8;
                Semester semester = new Semester(title.substring(semPos, semPos + 5));
                String courseNr = title.substring(nrPos, nrPos + 6);
                title = title.substring(0, nrPos - 2);

                LVA lva = new LVA(Integer.parseInt(courseNr), semester, handler);

                grades.add(new Grade(lva, date, ects, swst));
            }

            Grade[] gradeList = new Grade[grades.size()];

            handler.logout();

            return grades.toArray(gradeList);
        } catch (Exception e) {
            logger.error("getGrades: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get all currently available semesters
     *
     * @return A list of all currently available semesters.
     * @throws Exception Passed through exceptions
     */
    public Semester[] getSemesters() throws Exception {
        logger.info("Commencing getSemester");

        Semester[] sems = new Semester[10];

        try {
            handler.login();
            String source =
                    handler.getHTML("https://www.kusss.jku.at/kusss/timetable-start.action");
            handler.logout();

            source = source.substring(
                    source.indexOf("<label class=\"hideme\" for=\"term\">Semester</label>"));
            int j = 0;

            for (int i = 0; i < 10; i++) {
                j = source.indexOf("<option value=\"2") + 15;
                source = source.substring(j);
                sems[i] = new Semester(source.substring(0, 5));
            }

            logger.info("getSemester successful");

            return sems;

        } catch (Exception e) {
            logger.error("getSemesters crashed: \"" + e.getMessage() + "\"");
            throw e;
        }
    }

}
