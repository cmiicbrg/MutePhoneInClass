package at.dAuzinger.kusssApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;

/**
 * A class that handles all requests to the KUSSS and manages the session
 * @author David Auzinger
 * @version 1.0
 */
public class KUSSSHandler {
	String uid;
	String pwd;
	public Logger logger;
	CookieManager cookieMan;
	
	
	/**
	 * Default constructor
	 * @param uid_ The user id, e.g. "k1234567"
	 * @param pwd_ The user password
	 * @param logger_ An object of the type Logger, used to log events
	 */
	public KUSSSHandler(String uid_, String pwd_, Logger logger_)
	{
		this.uid = uid_;
		this.pwd = pwd_;
		this.logger = logger_;
		cookieMan = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
	}
	
	/**
	 * Starts a session using the given user credentials
	 * @throws Exception Either error because of wrong user credentials or passed trough error
	 */
	public void login() throws Exception
	{
		logger.info("Commencing login");
		
		try
		{
			String data = URLEncoder.encode("j_username", "UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8");
	        data += "&" + URLEncoder.encode("j_password", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8");
	        data += "&" + URLEncoder.encode("submit", "UTF-8") + "=" + URLEncoder.encode("login", "UTF-8");
			
	        CookieHandler.setDefault(cookieMan);
	        
	        URL url = new URL("https://www.kusss.jku.at/kusss/login.action");
			URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        wr.flush();
	        
	        InputStream response = conn.getInputStream();
	        
	        String html = streamToString(response);
	        
	        if(html.contains("<span class=\"hideme\">Angemeldet als</span>"))
	        {
	        	logger.info("Login successful");
	        	return;
	        }
	        else
	        {
	        	throw new Exception("Could not verify credentials, username and/or password are wrong");
	        }
		}
		catch(Exception e)
		{
			logger.error("Login crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}

	/**
	 * Gets the html source for a given webaddress
	 * @param url The url for the request
	 * @return The html source of the requested url
	 * @throws Exception Either not logged in error or passed through error
	 */
	public String getHTML(String url) throws Exception 
	{
		logger.info("Commencing getHTML: " + url);
		try
		{ 
	        if(!verifyLogin())
	        {
	        	throw new Exception("You have to login to access KUSSS");
	        }
	        else
	        {
	        	CookieHandler.setDefault(cookieMan);
				
				URLConnection conn = new URL(url).openConnection();
		        InputStream response = conn.getInputStream();
		        String html = streamToString(response);
		        
	        	logger.info("getHTML successful");
	        	return html;
	        }
	        
		}
		catch(Exception e)
		{
			logger.error("getHTML crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	/**
	 * Gets the html source for a given webaddress and submits POST parameters
	 * @param url The url for the request
	 * @param params Parameter pairs. The array has to have the Dimensions [k][2] where k is the number of pairs contained within the array. [i][0] contains the name of the parameter, [i][1] the value.
	 * @return The html source of the requested url
	 * @throws Exception Either not logged in error or passed through error
	 */
	public String getHTML(String url, String[][] params) throws Exception 
	{
		logger.info("Commencing getHTML: " + url);
		try
		{ 
	        if(!verifyLogin())
	        {
	        	throw new Exception("You have to login to access KUSSS");
	        }
	        else
	        {
	        	StringBuilder sb = new StringBuilder();
				for(String[] paramPair : params)
				{
					sb.append(URLEncoder.encode(paramPair[0], "UTF-8") + "=" + URLEncoder.encode(paramPair[1], "UTF-8"));
				}
				
				String data = sb.toString();
	        	
	        	CookieHandler.setDefault(cookieMan);
				URLConnection conn = new URL(url).openConnection();
		        conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		        wr.write(data);
		        wr.flush();
		        
		        InputStream response = conn.getInputStream();
		        
		        String html = streamToString(response);
		        
	        	logger.info("getHTML successful");
	        	return html;
	        }
	        
		}
		catch(FileNotFoundException e)
		{
			logger.info("getHTML FileNotFoundException (If there is NO note below to ignore this error this is a serious error)");
			throw e;
		}
		catch(Exception e)
		{
			logger.error("getHTML crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	
	/**
	 * Gets the html source for a given webaddress with a given referrer
	 * @param url The url for the request
	 * @param referrer The referrer 
	 * @return The html source of the requested url
	 * @throws Exception Either not logged in error or passed through error
	 */
	public String getHTML(String url, String referrer) throws Exception 
	{
		logger.info("Commencing getHTML with ref: " + url);
		try
		{ 
	        if(!verifyLogin())
	        {
	        	throw new Exception("You have to login to access KUSSS");
	        }
	        else
	        {
	        	CookieHandler.setDefault(cookieMan);
				
				URLConnection conn = new URL(url).openConnection();
				conn.addRequestProperty("REFERER", referrer);
		        InputStream response = conn.getInputStream();
		        String html = streamToString(response);
		        
	        	logger.info("getHTML successful");
	        	return html;
	        }
	        
		}
		catch(Exception e)
		{
			logger.error("getHTML crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	
	/**
	 * Downloads a file to a give destination 
	 * @param url The url of the file to download
	 * @param destination The location where the file should be saved
	 * @throws Exception Either not logged in error or passed through error
	 */
	public void getFile(String url, String destination) throws Exception
	{
		logger.info("Commencing getFile: " + url + ", " + destination);
		try
		{ 
	        if(!verifyLogin())
	        {
	        	throw new Exception("You have to login to access KUSSS");
	        }
	        else
	        {
	        	/*
	        	 * Mostly foreign code: http://www.jguru.com/faq/view.jsp?EID=13198
	        	 */
				URLConnection connection = new URL(url).openConnection();
				InputStream stream = connection.getInputStream();
				BufferedInputStream in = new BufferedInputStream(stream);
				FileOutputStream file = new FileOutputStream(destination);
				BufferedOutputStream out = new BufferedOutputStream(file);
				
				int i;
				while ((i = in.read()) != -1)
				{
		    		out.write(i);
				}
				out.flush();
				
				logger.info("getFile successful");
	        }
	        
		}
		catch(Exception e)
		{
			logger.error("getFile crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	/**
	 * Queries the server for a given url and returns the filename in the HTTP header
	 * @param url The url where the file can be found
	 * @return The actual name of the file at the given location
	 * @throws Exception Either not logged in error or passed through error 
	 */
	public String getFileName(String url) throws Exception
	{
		logger.info("Commencing getFileName: " + url);
		try
		{ 
	        if(!verifyLogin())
	        {
	        	throw new Exception("You have to login to access KUSSS");
	        }
	        else
	        {
	        	CookieHandler.setDefault(cookieMan);
				
				URLConnection conn = new URL(url).openConnection();
				String name = conn.getHeaderField("Content-Disposition");
				name = name.substring(22);
				name = name.substring(0, name.length() - 1);
		        
	        	logger.info("getFileName successful");
	        	return name;
	        }
	        
		}
		catch(Exception e)
		{
			logger.error("getFileName crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	/**
	 * Ends the current session
	 * @throws Exception Passed through error
	 */
	public void logout() throws Exception
	{
		logger.info("Commencing logout");
		try
		{
			CookieHandler.setDefault(cookieMan);
			
			new URL("https://www.kusss.jku.at/kusss/logout.action").openConnection();
			
	        logger.info("Logout successful");
		}
		catch(Exception e)
		{
			logger.error("Logout crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	/**
	 * Checks whether the current session is active or not
	 * @return True when logged in, false otherwise
	 * @throws Exception Passed through error
	 */
	private boolean verifyLogin() throws Exception
	{
		try
		{
			CookieHandler.setDefault(cookieMan);
			
			URLConnection conn = new URL("https://www.kusss.jku.at/kusss/timetable-start.action").openConnection();
	        
	        InputStream response = conn.getInputStream();
	        
	        String html = streamToString(response);
	        
	        if(html.contains("ssen Sie sich im System anmelden."))
	        {
	        	return false;
	        }
	        else
	        {
	        	return true;
	        }
	        
		}
		catch(Exception e)
		{
			logger.error("verifyLogin crashed: \"" + e.getMessage() + "\"");
			throw e;
		}
	}
	
	/**
	 * A method to convert an InputStream into a String
	 * @param is An InputStream that is to be converted to a String
	 * @return The string contained by the InputStream
	 * @throws IOException 
	 */
	private static String streamToString(InputStream is) throws IOException
	{
        /*
         * Foreign code, source: http://www.kodejava.org/examples/266.html
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
}

