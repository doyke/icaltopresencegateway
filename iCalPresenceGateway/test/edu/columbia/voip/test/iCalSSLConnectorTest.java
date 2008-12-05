package edu.columbia.voip.test;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.*;

import net.fortuna.ical4j.connector.*;
import net.fortuna.ical4j.connector.dav.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;

public class iCalSSLConnectorTest extends TestCase {

	public static final String 	PRODID = "-//John Morales//iCal4j Connector 1.0//EN";
	
	/** SSL */
	private static final String _ssl_username = "voipproject1";
	private static final char[] _ssl_password = "funvoip".toCharArray();
	public static final String 	SSL_HOST = "calendar.cs.columbia.edu";
	public static final int 	SSL_PORT = 8443;
	public static final String 	SSL_BASE_STORE_PATH = "/calendars/groups/voipprojectgroup2/calendar/";
	
	//private static Log LOG = LogFactory.getLog(iCalConnectorTester.class);
	
	public void testSSLTest()
	{
		try
		{
			CalDavCalendarStore calendarStore = 
				new CalDavCalendarStore(PRODID, SSL_HOST, SSL_PORT, Protocol.getProtocol("https"), SSL_BASE_STORE_PATH);
			assertNotNull("Couldn't create CalDavCalendarStore?", calendarStore);
			
			calendarStore.connect(_ssl_username, _ssl_password);
			System.out.println("Connected.");
			
			List<String> uidPaths = calendarStore.getCalendarUidPaths();
			assertNotNull("No ics files?", uidPaths);
			
			for (Iterator<String> iter = uidPaths.iterator(); iter.hasNext(); )
			{
				CalDavCalendarCollection cc = (CalDavCalendarCollection)calendarStore.getCollection(iter.next());
				assertNotNull(cc);
		        
				Calendar calendar = cc.getCalendar();
		        assertNotNull(calendar);
			}			
			calendarStore.disconnect();
		}
		catch (Exception e) { e.printStackTrace(); }
		System.out.println("Disconnected");
	}

	private void dumpCalendar(Calendar calendar) 
	{
		if (calendar == null)
		{
			System.err.println("dumpCalendar: calendar is null. returning.");
			return;
		}
		
		System.out.println("=-=- DUMPING CALENDAR FIELDS =-=-");
		for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            System.out.println("Component [" + component.getName() + "]");

            for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                Property property = (Property) j.next();
                System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
                if (property.getName().equals("DTSTART"))
                {
                	try {
                		Date d = new Date(property.getValue());	
                		System.err.println("date: " + d.toGMTString());
                	}
                	catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
	}
	
}
