/*
 * ServerParameters.java
 *
 * Created on November 1, 2008, 7:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

/**
 *
 * @author jmoral
 */
public class ServerParameters {
    
    /** Filename of server log that record debug messages/events. */
    public static String 		LOGFILE = "ical_gateway.log";
    
    /** I guess this will be changed later to the port on which iCal messages are sent? */
    public static int 			REGISTRATION_PORT = 1783;
    
    /** Hostname where the iCalendar server resides */
    public static String 		ICALENDAR_HOSTNAME = "calendar.cs.columbia.edu";
    
    /** iCalendar server destination port */
    public static int 			ICALENDAR_PORT = 8443;
    
    /** Thread pool size for polling registered users */
    public static int 			THREAD_POOL_SIZE = 30;
    
    /** Interval, measured in milliseconds, between when the calendar server is polled 
     * for new calendar events. */
    public static long			POLL_INTERVAL = 30000;
    
    /** Whether the iCalendar server is expecting SSL. */
    public static boolean 		ICALENDAR_USE_SSL = true;

    /** Hostname where mysql is running */
	public static String 		MYSQL_HOSTNAME = "localhost";
    
	/** Name of mysql database for storing the gateway registrations. */
	public static String 		MYSQL_REGISTRATION_DB = "icalgateway";

	/** Port on which mysql server is listening */
	public static int 			MYSQL_PORT = 3306;
	
	/** Mysql username for MYSQL_REGISTRATION_DB database */
	public static String		MYSQL_USERNAME = "root";
	
	/** Mysql password for MYSQL_REGISTRATION_DB database */
	public static String		MYSQL_PASSWORD = "";
	

	public static String getParametersString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Gateway Parameter Summary\n");
		buffer.append("LOGFILE:               " + ServerParameters.LOGFILE + "\n");
		buffer.append("REGISTRATION_PORT:     " + ServerParameters.REGISTRATION_PORT + "\n");
		buffer.append("ICALENDAR_HOSTNAME:    " + ServerParameters.ICALENDAR_HOSTNAME + "\n");
		buffer.append("ICALENDAR_PORT:        " + ServerParameters.ICALENDAR_PORT + "\n");
		buffer.append("THREAD_POOL_SIZE:      " + ServerParameters.THREAD_POOL_SIZE + "\n");
		buffer.append("POLL_INTERVAL:         " + ServerParameters.POLL_INTERVAL + "\n");
		buffer.append("ICALENDAR_USE_SSL:     " + ServerParameters.ICALENDAR_USE_SSL + "\n");
		buffer.append("MYSQL_HOSTNAME:        " + ServerParameters.MYSQL_HOSTNAME + "\n");
		buffer.append("MYSQL_REGISTRATION_DB: " + ServerParameters.MYSQL_REGISTRATION_DB + "\n");
		buffer.append("MYSQL_PORT:            " + ServerParameters.MYSQL_PORT + "\n");
		buffer.append("MYSQL_USERNAME:        " + ServerParameters.MYSQL_USERNAME + "\n");
		buffer.append("MYSQL_PASSWORD:        " + ServerParameters.MYSQL_PASSWORD + "\n");
		
		return buffer.toString();
	}
	
    /** parameters cannot be instantiated-- just accessed statically/globally */
    private ServerParameters() {}
}
