package at.dAuzinger.kusssApi;

/**
 * Class that holds news for an LVA
 * @author David Auzinger
 * @version 1.0
 */
public class LVANews {
	String title;
	String content;
	
	/**
	 * Default constructor
	 * @param title_ The news-title
	 * @param content_ The news-content
	 */
	public LVANews(String title_, String content_)
	{
		title = title_;
		content = content_;
	}
	
	/**
	 * Gives the title of the news represented by the LVANews object
	 * @return The title
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Gives the content of the news represented by the LVANews object
	 * @return The content
	 */
	public String getContent()
	{
		return content;
	}
}
