package at.dAuzinger.kusssApi;

import java.util.Comparator;

/**
 * A comparator implementation to compare DateTimes. Earlier DateTimes are lower
 * @author David Auzinger
 * @version 1.0
 * @see Comparator
 */
public class DateTimeComparator implements Comparator<DateTime>
{
	public int compare(DateTime dt1, DateTime dt2)
	{
		if(dt1.year < dt2.year)
		{
			return -1;
		}
		else if(dt1.year == dt2.year)
		{
			if(dt1.month < dt2.month)
			{
				return -1;
			}
			else if(dt1.month == dt2.month)
			{
				if(dt1.day < dt2.day)
				{
					return -1;
				}
				else if(dt1.day == dt2.day)
				{
					if(dt1.hour < dt2.hour)
					{
						return -1;
					}
					else if(dt1.hour == dt2.hour)
					{
						if(dt1.minute < dt2.minute)
						{
							return -1;
						}
						else if(dt1.minute == dt2.minute)
						{
							if(dt1.second < dt2.second)
							{
								return -1;
							}
							else if(dt1.second == dt2.second)
							{
								return 0;
							}
						}
					}
				}
			}
		}
		return 1;
	}
}
