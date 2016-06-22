package at.dAuzinger.kusssApi;

import java.util.ArrayList;

/**
 * Class representing a single LVA
 * @author David Auzinger
 * @version 1.0
 */
public class LVA
{
	String url;
	String name;
	String teacher;
	String homepage = null;
	String infos = null;
	String email = null;
	LVANews[] newsList = null;
	File[] fileList= null;
	KUSSSHandler handler;
	boolean fullyInitialized = false;
	boolean initialized = false;
	boolean preParametrized = false;
	String residueSource;
	TimeTable events = null;
	String lvaType = null;
	String lvaNr = null;
	
	/**
	 * Returns the url that points to the LVA in the KUSSS
	 * @return The url
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object or the value has not been passed on with the fittin constructor.
	 */
	public String getLvaNr() throws Exception
	{
		if(initialized || preParametrized)
		{
			return lvaNr;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized or preParametrized");
		}
	}
	
	/**
	 * Returns the url that points to the LVA in the KUSSS
	 * @return The url
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object or the value has not been passed on with the fittin constructor.
	 */
	public String getUrl() throws Exception
	{
		if(initialized || preParametrized)
		{
			return url;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized or preParametrized");
		}
	}
	
	/**
	 * Returns the name of the LVA
	 * @return The name
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object or the value has not been passed on with the fittin constructor.
	 */
	public String getName() throws Exception
	{
		if(initialized || preParametrized)
		{
			return name;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized or preParametrized");
		}
	}
	
	/**
	 * Returns the teachers name
	 * @return The teacher
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object or the value has not been passed on with the fittin constructor.
	 */
	public String getTeacher() throws Exception
	{
		if(initialized || preParametrized)
		{
			return teacher;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized or preParametrized");
		}
	}
	
	/**
	 * Returns the institutes/LVAs homepage, if given (otherwise null)
	 * @return The homepage
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object.
	 */
	public String getHomepage() throws Exception
	{
		if(initialized)
		{
			return homepage;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized");
		}
	}
	
	/**
	 * Returns the LVAs infos, if given (otherwise null)
	 * @return The infos
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object.
	 */
	public String getInfos() throws Exception
	{
		if(initialized)
		{
			return infos;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized");
		}
	}
	
	/**
	 * Returns the teachers/secretaries email, if given (otherwise null)
	 * @return The email
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object.
	 */
	public String getEmail() throws Exception
	{
		if(initialized)
		{
			return email;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized");
		}
	}
	
	
	/**
	 * Returns the LVAType. Is null if reading the LVAType failed during the initialize Method
	 * @return The LVAType
	 * @throws Exception Gives an error if the initialize method has not been called on the LVA object.
	 */
	public String getLvaType() throws Exception
	{
		if(initialized)
		{
			return lvaType;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been initialized");
		}
	}
	
	/**
	 * Sets the LVAType of the LVA
	 * @param lvaType_ The LVAType
	 */
	public void setLvaType(String lvaType_)
	{ 
		this.lvaType = lvaType_;
	}
	
	/**
	 * Sets the url that points to the LVA in the KUSSS
	 * @param url_ The url
	 */
	public void setUrl(String url_)
	{
		this.url = url_;
	}
	
	/**
	 * Sets the name of the LVA
	 * @param name_ The name of the LVA
	 */
	public void setName(String name_)
	{
		this.name = name_;
	}
	
	/**
	 * Sets the teachers name
	 * @param teacher_ The teachers name
	 */
	public void setTeacher(String teacher_)
	{
		this.teacher = teacher_;
	}
	
	/**
	 * Sets the institutes/LVAs homepage
	 * @param homepage_ The homepage
	 */
	public void setHomepage(String homepage_)
	{
		this.homepage = homepage_;
	}
	
	/**
	 * Sets the LVA infos
	 * @param infos_ The infos
	 */
	public void setInfos(String infos_)
	{
		this.infos = infos_;
	}
	
	/**
	 * Sets the teachers/secretaries email
	 * @param email_ The email to set
	 */
	public void setEmail(String email_)
	{
		this.email = email_;
	}
	
	/**
	 * Returns the LVA news, if given (otherwise null)
	 * @return The news
	 * @throws Exception Gives an error if the fullyInitialize method has not been called on the LVA object.
	 */
	public LVANews[] getNews() throws Exception
	{
		if(fullyInitialized)
		{
			return newsList;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been fully initialized");
		}
	}
	
	
	/**
	 * Returns the LVA files, if given (otherwise null)
	 * @return The files
	 * @throws Exception Gives an error if the fullyInitialize method has not been called on the LVA object.
	 */
	public File[] getFiles() throws Exception
	{
		if(fullyInitialized)
		{
			return fileList;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been fully initialized");
		}
	}
	
	/**
	 * Returns the LVA events, if given
	 * @return The events
	 * @throws Exception Gives an error if the fullyInitialize method has not been called on the LVA object.
	 */
	public TimeTable getTimeTable() throws Exception
	{
		if(fullyInitialized)
		{
			return events;
		}
		else
		{
			throw new Exception("The LVA instance has not yet been fully initialized");
		}
	}
	
	/**
	 * Returns the next LVA event
	 * @param includeCurrent Tell the method whether to include an currently ongoing event or not
	 * @return The event
	 * @throws Exception Gives an error if the fullyInitialize method has not been called on the LVA object.
	 */
	public Event getNext(boolean includeCurrent) throws Exception
	{
		if(fullyInitialized)
		{
			return events.getNext(includeCurrent);
		}
		else
		{
			throw new Exception("The LVA instance has not yet been fully initialized");
		}
	}
	
	/**
	 * Returns the next [count] LVA events
	 * @param includeCurrent Tell the method whether to include an currently ongoing event or not
	 * @param count The number of events you want to get
	 * @return A TimeTable containing the next [count] events
	 * @throws Exception Gives an error if the fullyInitialize method has not been called on the LVA object.
	 */
	public TimeTable getNext(boolean includeCurrent, int count) throws Exception
	{
		if(fullyInitialized)
		{
			return events.getNext(includeCurrent, count);
		}
		else
		{
			throw new Exception("The LVA instance has not yet been fully initialized");
		}
	}
	
	/**
	 * Alternative constructor using a semester and the course-number
	 * @param courseNr The course number for the LVA
	 * @param sem The semester for the LVA you are interested in
	 * @param handler_ A KUSSSHandler object
	 */
	public LVA(int courseNr, Semester sem, KUSSSHandler handler_)
	{
		this("https://www.kusss.jku.at/kusss/sz-lvadetail-overview.action?courseId=" + courseNr + "%2C" + sem.getShort(), handler_);
	}
	
	/**
	 * Alternative constructor allowing to set certain fields
	 * @param url_ The url pointing to the LVA in the KUSSS
	 * @param handler_ A KUSSSHandler object
	 * @deprecated Use LVA(String url_, KUSSSHandler handler_) instead and set name and teacher with setTeacher(String teacher_) and setName(String name_)
	 */
	@Deprecated
	public LVA(String url_, String name_, String teacher_, KUSSSHandler handler_)
	{
		handler_.logger.warn("LVA(String url_, String name_, String teacher_, KUSSSHandler handler_) is deprecated, see the Javadoc for additional infos");
		url = url_;
		handler = handler_;
		name = name_;
		int found = url.indexOf("courseId=");
		lvaNr = url.substring(found, found + 6);
	}
	
	/**
	 * Standard constructor
	 * @param url_ The url pointing to the LVA in the KUSSS
	 * @param handler_ A KUSSSHandler object
	 */
	public LVA(String url_, KUSSSHandler handler_)
	{
		url = url_;
		handler = handler_;
		int found = url.indexOf("courseId=");
		lvaNr = url.substring(found, found + 6);
	}
	
	/**
	 * Initializes as much values as possible with a single query (name, teacher, homepage, infos, email)
	 * @throws Exception Passed through exceptions
	 */
	public void initialize() throws Exception
	{
		if(!initialized)
		{
			try
			{
				handler.login();
				
				KUSSS.switchSemester(handler, new Semester(url.substring(url.length() - 5)));
				
				String source = handler.getHTML(url);
				
				if(source.indexOf("KUSSS | Kursinfos") == -1)
				{
					throw new Exception("The given url does not point to a LVA");
				}
				
				//System.out.println(source);
				source = source.substring(source.indexOf("<!-- ww:include \"/WEB-INF/jsp/sz/lvadetail/inc/base-courseinformation.inc.jsp\" -->"));
				source = source.substring(source.indexOf("<h3>") + 4);
				name = source.substring(0, source.indexOf("<small>")).trim().replace("\n", "").replace("\r", "").replace("	", " ");
				
				if(name.endsWith(LVAType.VO))
				{
					lvaType = LVAType.VO;
				}else if(name.endsWith(LVAType.UE))
				{
					lvaType = LVAType.UE;
				}else if(name.endsWith(LVAType.KV))
				{
					lvaType = LVAType.KV;
				}
				
				source = source.substring(source.indexOf("<td class=\"darkcell\" align=\"right\">LVA-LeiterIn:</td>"));
				source = source.substring(source.indexOf("<td class=\"lightcell\">") + 22);
				teacher = source.substring(0, source.indexOf("</td>")).trim();
				
				if(source.indexOf("<td nowrap=\"nowrap\" class=\"darkcell\" align=\"right\" >Infos:</td>") != -1)
				{
					source = source.substring(source.indexOf("<td nowrap=\"nowrap\" class=\"darkcell\" align=\"right\" >Infos:</td>"));
					source = source.substring(source.indexOf("<td class=\"lightcell\" colspan=\"5\">") + 34);
					infos = source.substring(0, source.indexOf("</td>")).trim();
					if(infos.contentEquals("{}"))
					{
						infos = null;
					}
				}
				
				if(source.indexOf("<td nowrap=\"nowrap\" class=\"darkcell\" align=\"right\" >URL:</td>") != -1)
				{
					source = source.substring(source.indexOf("<td nowrap=\"nowrap\" class=\"darkcell\" align=\"right\" >URL:</td>"));
					source = source.substring(source.indexOf("<a href=\"") + 9);
					homepage = source.substring(0, source.indexOf("\"")).trim();
				}
				
				if(source.indexOf("<td nowrap=\"nowrap\" class=\"darkcell\" align=\"right\" >Email:</td>") != -1)
				{
					source = source.substring(source.indexOf("<td nowrap=\"nowrap\" class=\"darkcell\" align=\"right\" >Email:</td>"));
					source = source.substring(source.indexOf("<a href=\"mailto:") + 16);
					source = source.substring(source.indexOf("\">") + 2);
					email = source.substring(0, source.indexOf("</a>")).trim();
				}
				
				residueSource = source;
				
				handler.logout();
				
				
			}
			catch (Exception e)
			{
				throw e;
			}
			
			initialized = true;
		}
	}
	
	/**
	 * Initializes all available variables for the LVA
	 * @throws Exception passed through exceptions
	 */
	public void fullyInitialize() throws Exception
	{
		if(!fullyInitialized)
		{
			if(!initialized)
			{
				initialize();
			}
			
			String source = new String(residueSource);
			
			if(source.indexOf("Keine News verf") == -1)
			{
				ArrayList<String> linkArrList = new ArrayList<String>();
				source = source.substring(source.indexOf("ge\" align=\"top\"/> News</strong>"));
				while(source.indexOf("<img src=\"/common/kusss/pics/subinfo_closed.gif\"") != -1)
				{
					source = source.substring(source.indexOf("<a href=\"sz-lvadetail-overview.action") + 9);
					linkArrList.add(source.substring(0, source.indexOf("\">")));
					source = source.substring(source.indexOf("<img src=\"/common/kusss/pics/subinfo_closed.gif\"") + 5);
				}
				String[] links = linkArrList.toArray(new String[linkArrList.size()]);
				
				String src;
				
				ArrayList<LVANews> newsArrList = new ArrayList<LVANews>();
				
				LVANews news;
				String newsTitle;
				String newsText;
				
				for(String link : links)
				{
					link = link.replace("&amp;", "&");
					
					src = handler.getHTML("https://www.kusss.jku.at/kusss/" + link);
					src = src.substring(src.indexOf("<td class=\"subinfo\" colspan=\"3\">") - 400);
					src = src.substring(src.indexOf("<strong>") + 8);
					newsTitle = src.substring(0, src.indexOf("</strong>")).trim();
					src = src.substring(src.indexOf("<td class=\"subinfo\" colspan=\"3\">") + 36);
					newsText = src.substring(0, src.indexOf("</td>")).trim();
					news = new LVANews(newsTitle, newsText);
					newsArrList.add(news);
				}
				newsList = newsArrList.toArray(new LVANews[newsArrList.size()]);
			}
			
			
			if(source.indexOf("Keine Dateien verf") == -1)
			{
				File file;
				String fileTitle;
				String fileLink;
				ArrayList<File> fileArrList = new ArrayList<File>();
				
					source = source.substring(source.indexOf("Dateien\" align=\"top\"/> Dateien</strong>"));
				while(source.indexOf("<a href=\"download-file.action") != -1)
				{
					source = source.substring(source.indexOf("<a href=\"download-file.action") + 9);
					fileLink = "https://www.kusss.jku.at/kusss/" + source.substring(0, source.indexOf("\">"));
					fileLink = fileLink.replace("&amp;", "&");
					source = source.substring(source.indexOf("\">") + 2);
					fileTitle = source.substring(0, source.indexOf("</a>")).trim();
					file = new File(fileLink, fileTitle, handler);
					fileArrList.add(file);
				}
				fileList = fileArrList.toArray(new File[fileArrList.size()]);
			}
			
			source = source.substring(source.indexOf("<span class=\"menu-item\">Kursinfos</span>"));
			source = source.substring(source.indexOf("href=\"") + 6);
			String eventsLink = source.substring(0, source.indexOf("\""));
			
			source = handler.getHTML("https://www.kusss.jku.at/kusss/" + eventsLink);
			source = source.substring(source.indexOf("<a href=\"ical-export-course.action") + 9);
			String icalLink = source.substring(0, source.indexOf("\">"));
			
			String ical = handler.getHTML("https://www.kusss.jku.at/kusss/" + icalLink);
			
			events = new TimeTable(ical, handler);
		}
		
		fullyInitialized = true;
	}
}
