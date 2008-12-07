package edu.columbia.voip.server.conf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.columbia.voip.server.ServerParameters;

public class ConfProcessor
{
	private static final String CONF_FILENAME = "gateway.conf";

	/**
	 * Parse gateway.conf configuration file and set ServerParameters 
	 * struct with any parameters.
	 * @throws ConfParseException
	 */
	public static void loadConfFile() throws ConfParseException
	{
		BufferedReader in = null; 
	
		try { in = new BufferedReader(new FileReader(CONF_FILENAME)); } 
		catch (FileNotFoundException e) { throw new ConfParseException(e); } 
		
		String line = null;
		do
		{
			try { line = in.readLine(); }
			catch (IOException e) { throw new ConfParseException(e); }
			
			if (line == null) 
				break;
			if (line.length() == 0 || line.startsWith("#"))
				continue; // comment line or new line, skip
			
			String[] arguments = line.split(" ");
			if (arguments.length != 2)
				throw new ConfParseException("Invalid number of arguments; expecting exactly two, key and value");
			
			String key = arguments[0];
			String value = arguments[1];
			setParameter(key, value);
		} while (true);
		
		try { in.close(); }
		catch (IOException e) { throw new ConfParseException(e); }
	}

	private static void setParameter(String key, String value) throws ConfParseException
	{
		try {
			if (key.equals("RegistrationListenPort"))
				ServerParameters.REGISTRATION_PORT = Integer.parseInt(value);
			else if (key.equals("ThreadPoolSize"))
				ServerParameters.THREAD_POOL_SIZE = Integer.parseInt(value);
			else if (key.equals("LogfileName"))
				ServerParameters.LOGFILE = value;
			else if (key.equals("PollInterval"))
				ServerParameters.POLL_INTERVAL = Long.parseLong(value);
			else if (key.equals("ICalendarHost"))
				ServerParameters.ICALENDAR_HOSTNAME = value;
			else if (key.equals("ICalendarPort"))
				ServerParameters.ICALENDAR_PORT = Integer.parseInt(value);
			else if (key.equals("ICalendarUseSSL"))
				ServerParameters.ICALENDAR_USE_SSL = Boolean.parseBoolean(value);
			else if (key.equals("MysqlHost"))
				ServerParameters.MYSQL_HOSTNAME = value;
			else if (key.equals("MysqlRegistrationDB"))
				ServerParameters.MYSQL_REGISTRATION_DB = value;
			else if (key.equals("MysqlPort"))
				ServerParameters.MYSQL_PORT = Integer.parseInt(value);
			else
				throw new ConfParseException("No such configuration parameter: '" + key + "'");
		}
		catch (NumberFormatException e) { throw new ConfParseException(e); }
	}
}
