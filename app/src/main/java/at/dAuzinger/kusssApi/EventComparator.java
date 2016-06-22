package at.dAuzinger.kusssApi;

import java.util.Comparator;

/**
 * A comparator implementation to compare Events. Earlier events are lower
 * @author David Auzinger
 * @version 1.0
 * @see Comparator
 */
public class EventComparator implements Comparator<Event>
{
	public int compare(Event e1, Event e2)
	{
		return new DateTimeComparator().compare(e1.startTime, e2.startTime);
	}

}
