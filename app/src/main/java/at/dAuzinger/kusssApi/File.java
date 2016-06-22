package at.dAuzinger.kusssApi;

/**
 * Class for handling the interaction with files offered in the KUSSS
 * @author David Auzinger
 * @version 1.0
 */
public class File {
	String url;
	String title;
	String filename;
	KUSSSHandler handler;
	
	/**
	 * Default constructor
	 * @param url_ The location of the file
	 * @param title_ A name assigned to the file (not the actual filename, but a more representing one, e.g. "Übung 1")
	 * @param handler_ A KUSSSHandler, needed to access the KUSSS
	 * @throws Exception Passed through errors
	 */
	public File(String url_, String title_, KUSSSHandler handler_) throws Exception
	{
		url = url_;
		title = title_;
		handler = handler_;
		
		handler.login();
		filename = handler.getFileName(url);
		handler.logout();
	}
	
	/**
	 * Method to get the file title
	 * @return The title assigned to the file
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * Method to get the filename
	 * @return The actual filename
	 */
	public String getFileName()
	{
		return filename;
	}
	
	/**
	 * Method to download the file represented by the File-object to a given destination
	 * @param destination Where the file should be saved
	 * @throws Exception Passed through errors
	 */
	public void download(String destination) throws Exception
	{
		handler.login();
		handler.getFile(url, destination);
		handler.logout();
	}
	
}
