package at.dAuzinger.kusssApi;

/**
 * Class representing a single event (e.g. a "Vorlesung")
 * @author David Auzinger
 * @version 1.0
 */
public class Event {
	DateTime startTime;
	Duration duration;
	String location;
	LVA lva;
	
	/**
	 * Standard constructor
	 * @param startTime_ The starttime of the event
	 * @param duration_ The duration of the event
	 * @param location_ The event location string
	 * @param lva_ The LVA for the event
	 */
	public Event(DateTime startTime_, Duration duration_, String location_, LVA lva_)
	{
		startTime = startTime_;
		duration = duration_;
		location = location_;
		lva = lva_;
	}
	
	/**
	 * Returns the LVA for the Event
	 * @return The LVA for the given Event object
	 */
	public LVA getLVA()
	{
		return lva;
	}
	
	/**
	 * Checks which of 2 given Event objects is earlier in time. Returns true if the element the function is called on is earlier than the parameter.
	 * @param comparison A second Event object to compare to
	 * @return True if the element the function is called on is earlier than the parameter. Otherwise (later, equal) false;
	 */
	public boolean isEarlier(Event comparison)
	{
		return this.startTime.isEarlier(comparison.startTime);
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public Duration getDuration() {
		return duration;
	}
}
