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
    public static long			POLL_INTERVAL = 10000;
    
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
	
	
    /** parameters cannot be instantiated-- just accessed statically/globally */
    private ServerParameters() {}
}
