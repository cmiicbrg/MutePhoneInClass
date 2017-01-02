package at.dAuzinger.kusssApi;

import java.util.GregorianCalendar;

/**
 * Class representing a semester
 * @author David Auzinger
 * @version 1.0
 */
public class Semester {
	String year;
	String typeShort;
	String typeLong;	
	
	
	/**
	 * Standard constructor
	 * @param semester Semester identifier, e.g. "2012W"
	 */
	public Semester(String semester)
	{
		year = semester.substring(0, 4);
		
		if(semester.endsWith("W"))
		{
			typeShort = "W";
			typeLong = "Wintersemester";
		}
		else
		{
			typeShort = "S";
			typeLong = "Sommersemester";
		}
	}

    public Semester() {
        GregorianCalendar current = new GregorianCalendar();

        int winterStartYear =
                current.before(new GregorianCalendar(current.get(GregorianCalendar.YEAR), 2, 15)) ?
                        current.get(GregorianCalendar.YEAR) - 1 :
                        current.get(GregorianCalendar.YEAR);
        int summerStartYear =
                current.after(new GregorianCalendar(current.get(GregorianCalendar.YEAR), 9, 1)) ?
                        current.get(GregorianCalendar.YEAR) + 1 :
                        current.get(GregorianCalendar.YEAR);
        GregorianCalendar summerStart =
                new GregorianCalendar(summerStartYear, 2, 15);
        GregorianCalendar winterStart =
                new GregorianCalendar(winterStartYear, 9, 1);

        if (current.after(summerStart) && current.before(winterStart)) {
            year = String.valueOf(summerStartYear);
            typeShort = "S";
            typeLong = "Sommersemester";
        } else {
            year = String.valueOf(winterStartYear);
            typeShort = "W";
            typeLong = "Wintersemester";
        }
    }

    /**
	 * Generates the short form of the String representative of the Semester
	 * @return The semester String-representative in short form, e.g. "2012W"
	 */
	public String getShort()
	{
		return year + typeShort;
	}
	
	/**
	 * Generates the long form of the String representative of the Semester
	 * @return The semester String-representative in long form, e.g. "Wintersemester 2012"
	 */
	public String getLong()
	{
			return typeLong + " " + year;
	}
}
