package at.dAuzinger.kusssApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Another class to simplify handling time
 * @author David Auzinger
 * @version 1.0
 */
public class Duration {
	public int years = 0, months = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
	
	/**
	 * Default constructor, initializes everything except hours and minutes with 0
	 * @param hours_ Hours
	 * @param minutes_ Minutes
	 */
	public Duration(int hours_, int minutes_)
	{
		hours = hours_;
		minutes = minutes_;
	}
	
	/**
	 * Alternative constructor, initializes the object with the time offset between UTC time and local time.
	 */
	public Duration()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		String time = sdf.format(cal.getTime());
		
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timeUTC = sdf.format(cal.getTime());
		 
		years = Integer.parseInt(time.substring(4, 8)) - Integer.parseInt(timeUTC.substring(4, 8));
		months = Integer.parseInt(time.substring(2, 4)) - Integer.parseInt(timeUTC.substring(2, 4));
		days = Integer.parseInt(time.substring(0, 2)) - Integer.parseInt(timeUTC.substring(0, 2));
		hours = Integer.parseInt(time.substring(8, 10)) - Integer.parseInt(timeUTC.substring(8, 10));
		minutes = Integer.parseInt(time.substring(10, 12)) - Integer.parseInt(timeUTC.substring(10, 12));
		seconds = Integer.parseInt(time.substring(12, 14)) - Integer.parseInt(timeUTC.substring(12, 14));
	}
	
	/**
	 * Extended constructor, initializes everything except hours, minutes and with 0
	 * @param hours_ Hours
	 * @param minutes_ Minutes
	 * @param seconds_ Seconds
	 */
	public Duration(int hours_, int minutes_, int seconds_)
	{
		hours = hours_;
		minutes = minutes_;
		seconds = seconds_;
	}
	
	/**
	 * Extended constructor, sets all variables
	 * @param years_ Years
	 * @param months_ Months
	 * @param days_ Days
	 * @param hours_ Hours
	 * @param minutes_ Minutes
	 * @param seconds_ Seconds
	 */
	public Duration(int years_, int months_, int days_, int hours_, int minutes_, int seconds_)
	{
		years = years_;
		months = months_;
		days = days_;
		hours = hours_;
		minutes = minutes_;
		seconds = seconds_;
	}
}
