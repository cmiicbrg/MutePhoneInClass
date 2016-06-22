package at.dAuzinger.kusssApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Class used to log, mainly for bug reports from users
 * @author David Auzinger
 * @version 1.0
 */
public class Logger {
	String logText;
	String stackTrace;
	
	
	/**
	 * Default constructor
	 */
	public Logger()
	{
		logText = "";
		stackTrace = "";
	}
	
	public void trace(Exception ex)
	{
		StringBuilder sb = new StringBuilder(stackTrace);
		StackTraceElement[] traces = ex.getStackTrace();
		
		for(StackTraceElement trace : traces)
		{
			sb.append(trace.toString());
			sb.append("\n");
		}
		sb.append("-------------------\n");
		stackTrace = sb.toString();
	}
	
	public void info(String text)
	{
		StringBuilder sb = new StringBuilder(logText);
		sb.append(now());
		sb.append("[INFO ]: ");
		sb.append(text);
		sb.append("\n");
		logText = sb.toString();
	}
	
	public void debug(String text)
	{
		StringBuilder sb = new StringBuilder(logText);
		sb.append(now());
		sb.append("[DEBUG]: ");
		sb.append(text);
		sb.append("\n");
		logText = sb.toString();
	}
	
	public void warn(String text)
	{
		StringBuilder sb = new StringBuilder(logText);
		sb.append(now());
		sb.append("[WARN ]: ");
		sb.append(text);
		sb.append("\n");
		logText = sb.toString();
	}
	
	public void error(String text)
	{
		StringBuilder sb = new StringBuilder(logText);
		sb.append(now());
		sb.append("[ERROR]: ");
		sb.append(text);
		sb.append("\n");
		logText = sb.toString();
	}
	
	public void fatal(String text)
	{
		StringBuilder sb = new StringBuilder(logText);
		sb.append(now());
		sb.append("[FATAL]: ");
		sb.append(text);
		sb.append("\n");
		logText = sb.toString();
	}
	
	/**
	 * Adds date, time, the given text and a new line to the log
	 * @param text The text to log
	 * @deprecated Use info, debug, warn, error or fatal instead
	 */
	@Deprecated
	public void log(String text)
	{
		warn("Logger.log() is deprecated, see the Javadoc for additional infos");
		StringBuilder sb = new StringBuilder(logText);
		sb.append(now());
		sb.append(": ");
		sb.append(text);
		sb.append("\n");
		logText = sb.toString();
	}
	
	/**
	 * Used to access the current log text
	 * @return The current contents of the log
	 */
	public String getLog()
	{
		return logText;
	}
	
	/**
	 * Empties the log
	 */
	public void clear()
	{
		logText = "";
	}
	
	/**
	 * Method to generate the date and time String prepended to every log
	 * @return Date and time in the following format: "dd.MM.yyy HH:mm:ss"
	 */
	private static String now() 
	{
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
	    return sdf.format(cal.getTime());
	}
}
