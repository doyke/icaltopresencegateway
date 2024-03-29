package edu.columbia.voip.test;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.*;

import net.fortuna.ical4j.connector.dav.*;
import net.fortuna.ical4j.model.Calendar;

public class iCalConnectorTest extends TestCase {

	public static final String 	PRODID = "-//John Morales//iCal4j Connector 1.0//EN";
	
	/** Non-SSL */
	private static final String _username = "admin";
	private static final char[] _password = "admin".toCharArray();
	public static final String 	HOST = "128.59.101.33";
	public static final int 	PORT = 8008;
	public static final String 	BASE_STORE_PATH = "/calendars/groups/group01/3F8850DB-DDCF-461E-9BED-DCCD45FDE8EF/";
	
	//private static Log LOG = LogFactory.getLog(iCalConnectorTester.class);
	
	public void testNonSSLTest()
	{
		try
		{
			CalDavCalendarStore calendarStore = 
						new CalDavCalendarStore(PRODID, HOST, PORT, Protocol.getProtocol("http"), BASE_STORE_PATH);
			assertNotNull("Couldn't create CalDavCalendarStore?", calendarStore);
			
			calendarStore.connect(_username, _password);
			System.out.println("Connected.");
			
			List<String> uidPaths = calendarStore.getCalendarUidPaths();
			assertNotNull("No ics files?", uidPaths);
			
			for (Iterator<String> iter = uidPaths.iterator(); iter.hasNext(); )
			{
				CalDavCalendarCollection cc = (CalDavCalendarCollection)calendarStore.getCollection(iter.next());
				assertNotNull(cc);
		        
				Calendar calendar = cc.getCalendar();
		        assertNotNull(calendar);
		        AllTests.dumpCalendar(calendar);
			}			
			calendarStore.disconnect();
		}
		catch (Exception e) { e.printStackTrace(); }
		System.out.println("Disconnected");
	}
}
