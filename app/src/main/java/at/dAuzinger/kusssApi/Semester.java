package at.dAuzinger.kusssApi;

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
