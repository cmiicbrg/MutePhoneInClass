package at.dAuzinger.kusssApi;

import java.util.ArrayList;
import java.util.Arrays;;

/**
 * A class handling multiple events
 * @author David Auzinger
 * @version 1.0
 */
public class TimeTable
{
	Event[] events;
	
	/**
	 * Standard constructor
	 * @param ical The ical string containing all events
	 * @param handler_ A KUSSSHandler object
	 * @throws Exception Passed through exceptions
	 */
	public TimeTable(String ical, KUSSSHandler handler_) throws Exception
	{
		ArrayList<Event> eventsList = new ArrayList<Event>();
		
		while(ical.indexOf("BEGIN:VEVENT") != -1)
		{
			
			ical = ical.substring(ical.indexOf("BEGIN:VEVENT") + 12);
			int index = ical.indexOf("DTSTART:") + 8;
			String startTime = ical.substring(index, index + 16);
			index = ical.indexOf("DTEND:") + 6;
			String endTime = ical.substring(index, index + 16);
			ical = ical.substring(ical.indexOf("SUMMARY:") + 8);
			String summary = ical.substring(0, ical.indexOf("DESCRIPTION:")).trim();
			String location = ical.substring(ical.indexOf("LOCATION:") + 9, ical.indexOf("END")).trim();
			
			index = summary.indexOf("Lva-LeiterIn:") - 16;
			String teacher = summary.substring(index + 30);
			String name = summary.substring(0, summary.indexOf("\\n"));
			String lvaNrText = summary.substring(index, index + 6);
			String semester = summary.substring(index + 7, index + 12);
			summary = summary.substring(0, index - 1).trim();
			
			int lvaNr = Integer.parseInt(lvaNrText);
			Semester sem = new Semester(semester);
			int startYear = Integer.parseInt(startTime.substring(0, 4));
			int startMonth = Integer.parseInt(startTime.substring(4, 6));
			int startDay = Integer.parseInt(startTime.substring(6, 8));
			int startHour = Integer.parseInt(startTime.substring(9, 11)) + 1;
			int startMinute = Integer.parseInt(startTime.substring(11, 13));
			
			int durationHour = Integer.parseInt(endTime.substring(9, 11)) - startHour;
			int durationMinute = Integer.parseInt(endTime.substring(11, 13)) - startMinute;
			
			if(durationMinute < 0)
			{
				durationMinute += 60;
				durationHour--;
			}
			
			LVA lva = new LVA(lvaNr, sem, handler_);
			lva.setTeacher(teacher);
			lva.setName(name);
			DateTime start = new DateTime(startYear, startMonth, startDay, startHour, startMinute, 0);
			start = start.add(new Duration());
			Duration duration = new Duration(durationHour, durationMinute);
			
			eventsList.add(new Event(start, duration, location, lva));
		}
		
		events = eventsList.toArray(new Event[eventsList.size()]);
		
		Arrays.sort(events, new EventComparator());
	}
	
	/**
	 * Alternative constructor, using a list of events instead of an ical string
	 * @param events_ The event-array
	 */
	public TimeTable(Event[] events_)
	{
		events = events_;
		Arrays.sort(events, new EventComparator());
	}
	
	/**
	 * Returns the next event
	 * @param includeCurrent Tell the method whether to include an currently ongoing event or not
	 * @return The event
	 */
	public Event getNext(boolean includeCurrent)
	{
		Event now = new Event(new DateTime(), null, null, null);
		
		for(int i = 0; i < events.length; i++)
		{
			if(now.isEarlier(events[i]))
			{
				if(includeCurrent)
				{
					int j = i - 5;
					if(j < 0)
					{
						j = 0;
					}
					
					for(; j < i; j++)
					{
						if(now.startTime.isEarlier(events[j].startTime.add(events[j].duration)))
						{
							return events[j];
						}
					}
				}
				return events[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns the next [count] events
	 * @param includeCurrent Tell the method whether to include an currently ongoing event or not
	 * @param count The number of events you want to get
	 * @return A TimeTable containing the next [count] events
	 */
	public TimeTable getNext(boolean includeCurrent, int count)
	{
		Event now = new Event(new DateTime(), null, null, null);
		
		for(int i = 0; i < events.length; i++)
		{
			if(now.isEarlier(events[i]))
			{
				int k = i;
				
				if(includeCurrent)
				{
					int j = i - 5;
					if(j < 0)
					{
						j = 0;
					}
					
					for(; j < i; j++)
					{
						if(now.startTime.isEarlier(events[j].startTime.add(events[j].duration)))
						{
							k = j;
						}
					}
				}
				
				ArrayList<Event> eventsList = new ArrayList<Event>();
				
				for(int l = 0; l < count && (l + k) < events.length; l++)
				{
					eventsList.add(events[k + l]);
				}
				
				return new TimeTable(eventsList.toArray(new Event[count]));
			}
		}
		return null;
	}
	
	/**
	 * Returns all events for a given day
	 * @param day The day you are interested in in a DateTime object. Hour, minute and second values are ignored
	 * @return A TimeTable object containing all events for the given day
	 */
	public TimeTable getDay(DateTime day)
	{
		day.hour = 0;
		day.minute = 0;
		day.second = 0;
		
		
		Event start = new Event(day, null, null, null);
		Event end = new Event(new DateTime(day.year, day.month, day.day, 23, 59, 59), null, null, null);
		
		for(int i = 0; i < events.length; i++)
		{
			if(start.isEarlier(events[i]))
			{
				ArrayList<Event> eventsList = new ArrayList<Event>();
				
				while(end.isEarlier(events[i]))
				{
					eventsList.add(events[i]);
					i++;
				}
				
				return new TimeTable(eventsList.toArray(new Event[eventsList.size()]));
			}
		}
		return null;
	}
}
