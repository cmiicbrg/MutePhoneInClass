package at.dAuzinger.kusssApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A class to handle dates and time in an easy way
 * @author David Auzinger
 * @version 1.0
 */
public class DateTime {
	public int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
	
	/**
	 * Default constructor
	 * Initializes year, month and day only. Everything else is initialized with 0
	 * @param year_ Year
	 * @param month_ Month
	 * @param day_ Day
	 */
	public DateTime(int year_, int month_, int day_)
	{
		year = year_;
		month = month_;
		day = day_;
	}
	
	/**
	 * Alternative constructor which initializes the object with the current time
	 */
	public DateTime()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		String time = sdf.format(cal.getTime());
		 
		year = Integer.parseInt(time.substring(4, 8));
		month = Integer.parseInt(time.substring(2, 4));
		day = Integer.parseInt(time.substring(0, 2));
		hour = Integer.parseInt(time.substring(8, 10));
		minute = Integer.parseInt(time.substring(10, 12));
		second = Integer.parseInt(time.substring(12, 14));
	}
	
	/**
	 * Extended constructor which can also initialize hour, minute and second
	 * @param year_ Year
	 * @param month_ Month
	 * @param day_ Day
	 * @param hour_ Hour
	 * @param minute_ Minute
	 * @param second_ Second
	 */
	public DateTime(int year_, int month_, int day_, int hour_, int minute_, int second_)
	{
		year = year_;
		month = month_;
		day = day_;
		hour = hour_;
		minute = minute_;
		second = second_;
	}
	
	/**
	 * Creates a new DateTime object that contains the Duration (parameter) subtracted from the current object.
	 * @param duration The duration that should be subtracted from the DateTime object
	 * @return The new DateTime object
	 */
	public DateTime subtract(Duration duration)
	{
		return new DateTime(year - duration.years, month - duration.months, day - duration.days, hour - duration.hours, minute - duration.minutes, second - duration.seconds);
	}
	
	/**
	 * Creates a new DateTime object that contains the Duration (parameter) added to the current object.
	 * @param duration The duration that should be added to the DateTime object
	 * @return The new DateTime object
	 */
	public DateTime add(Duration duration)
	{
		return new DateTime(year + duration.years, month + duration.months, day + duration.days, hour + duration.hours, minute + duration.minutes, second + duration.seconds);
	}

	/**
	 * Checks which of 2 given DateTime objects is earlier in time. Returns true if the element the function is called on is earlier than the parameter.
	 * @param comparison A second DateTime object to compare to
	 * @return True if the element the function is called on is earlier than the parameter. Otherwise (later, equal) false;
	 */
	public boolean isEarlier(DateTime comparison)
	{
		if(new DateTimeComparator().compare(this, comparison) == -1)
		{
			return true;
		}
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return day + "." + month + "." + year;
	}
}
