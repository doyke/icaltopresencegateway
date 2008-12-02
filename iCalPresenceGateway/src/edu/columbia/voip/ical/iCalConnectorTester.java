package edu.columbia.voip.ical;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.*;
import net.fortuna.ical4j.connector.*;
import net.fortuna.ical4j.connector.dav.*;
import net.fortuna.ical4j.connector.jcr.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.Calendars;

public class iCalConnectorTester {

	/** Google Credentials */
	//private static final String _username = "voipsec.team9@gmail.com";
	//private static final char[] _password = "voice1234".toCharArray();
	
	/** Milind iCalendar server creds */
	private static final String _username = "admin";
	private static final char[] _password = "admin".toCharArray();
	
	
//	public static final String 	PRODID = "-//John Morales//iCal4j Connector 1.0//EN";
//	public static final String 	HOST = "www.columbia.edu";
//	public static final int 	PORT = 80;
//	public static final String 	BASE_STORE_PATH = "/~jm2873/calendar/events.ics";
	
	//public static final String 	HOST = "calendar.cs.columbia.edu";
	//public static final String 	BASE_STORE_PATH = "/calendars/groups/voipprojectgroup2/calendar/";
	
	private static CalDavCalendarStore _calendarStore = null;
	
	public static final String 	PRODID = "-//John Morales//iCal4j Connector 1.0//EN";
	public static final String 	HOST = "209.2.226.64";
	public static final int 	PORT = 8008;
	public static final String 	BASE_STORE_PATH = "/calendars/groups/group01/calendar/";
	public static final String	CALENDAR_ID = "myCalendars";
	
	
	/** From unit test */
	private String collectionId = "1886E30D-2166-4AA4-9A7C-260C0EA59499.ics";
	private String uid = "1886E30D-2166-4AA4-9A7C-260C0EA59499";
    private String description = "My collection of calendars";
    private String displayName = "My Calendars";
    private String[] calendarUids;
	private String[] supportedComponents = {Component.VEVENT};
	   

	//private static Log LOG = LogFactory.getLog(iCalConnectorTester.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Testing...");
		new iCalConnectorTester();
		System.out.println("Exiting");
	}
	
	public iCalConnectorTester() throws Exception
	{
		_calendarStore = new CalDavCalendarStore(PRODID, HOST, PORT, Protocol.getProtocol("http"), BASE_STORE_PATH);
		_calendarStore.connect(_username, _password);
		System.out.println("Connected.");
		
		tryGetCalendar();
		
        /** FIXME: add calendar */
		//tryAddCalendar();
		
		/** FIXME: broken */
		//tryCalDavAddCalendar();
      
		_calendarStore.disconnect();
		System.out.println("Disconnected");
	}
	
	private void tryGetCalendar() throws Exception {
		CalDavCalendarCollection cc = (CalDavCalendarCollection)_calendarStore.getCollection(collectionId);
        if (cc == null)
        {
        	System.out.println("the calendar_id '" + collectionId + "' doesn't exist? Adding it");
        	cc = (CalDavCalendarCollection)_calendarStore.addCollection(collectionId, displayName, description, supportedComponents, null);
        }
        
        System.out.println("So far so good-- have calendar collection");
        
        dumpCalendarCollection(cc);
	}


	private void tryAddCalendar() throws Exception {
		CalendarCollection cc = _calendarStore.getCollection("myCalendars");
        if (cc != null)
        	throw new RuntimeException("thought myCalendars doesn't exist?");
        System.out.println("So far so good-- calendar doesn't already exist. About to add...");
        
        _calendarStore.addCollection("myCalendars");
        cc = _calendarStore.getCollection("myCalendars");
        if (cc == null)
        	throw new RuntimeException("couldn't create it?");
        
        System.out.println("SUCCESS! created calendar.");
	}
	
	private void tryCalDavAddCalendar() throws ObjectNotFoundException, ObjectStoreException {
		CalDavCalendarCollection c = (CalDavCalendarCollection)_calendarStore.getCollection(CALENDAR_ID);
		if (c == null)
		{
			System.out.println("CalDavCalendarCollection " + CALENDAR_ID + " was null! Adding a new calendar...");
			c = (CalDavCalendarCollection)_calendarStore.addCollection(	CALENDAR_ID);
			
			System.out.println("New calendar " + CALENDAR_ID + " added, which returned: " + c);
			
//			Set uidList = new HashSet();
//            Calendar[] uidCals = Calendars.split(testCal);
//            for (int i = 0; i < uidCals.length; i++) {
//                Uid uid = Calendars.getUid(uidCals[i]);
//                if (uid != null) {
//                    uidList.add(uid.getValue());
//                }
//            }
//            String[] calendarUids = (String[]) uidList.toArray(new String[uidList.size()]);
            
			
		}
		else
		{
			System.out.println("Calendar " + CALENDAR_ID + " exists!");
			Calendar[] calendars = c.getCalendars();
			if (calendars == null)
			{
				System.out.println("Crap-- calendars array is null.");
			}
			else
			{
				for (int i=0; i < calendars.length; i++)
				{
					Calendar calendar = calendars[i];
					if (calendar != null)
					{
						System.out.println("Calendar BEGIN: " + calendar.BEGIN);
						System.out.println("Calendar END: " + calendar.END);
					}
				}
			}
			
//			System.out.println("collection description: " + c.getDescription());
//			System.out.println("collection display name: " + c.getDisplayName());
		}
	}
	
	private void dumpCalendarCollection(CalendarCollection collection)
	{
		if (collection == null)
		{
			System.err.println("dumpCalendarCollection: collection is null. returning.");
			return;
		}
		
		System.out.println("=-=- DUMPING CALENDARCOLLECTION FIELDS =-=-");
		System.out.println("Description:                '" + collection.getDescription() + "'");
		System.out.println("Display Name:               '" + collection.getDisplayName() + "'");
		System.out.println("getMaxAttendeesPerInstance: '" + collection.getMaxAttendeesPerInstance() + "'");
		System.out.println("getMaxInstances:            '" + collection.getMaxInstances() + "'");
		//System.out.println("getMaxResourceSize:         '" + collection.getMaxResourceSize() + "'");
		System.out.println("getMinDateTime:             '" + collection.getMinDateTime() + "'");
		System.out.println("getSupportedComponentTypes: '" + collection.getSupportedComponentTypes() + "'");
		System.out.println("getSupportedMediaTypes:     '" + collection.getSupportedMediaTypes() + "'");
		System.out.println("getTimeZone:                '" + collection.getTimeZone() + "'");
		
		String[] componentTypes = collection.getSupportedComponentTypes();
		if (componentTypes == null)
			System.err.println("Supported Component Types is null");
		else
			for (int i=0; i < componentTypes.length; i++)
				System.out.println("Component Type " + i + ": '" + componentTypes[i] + "'");
		
		CalDavCalendarCollection caldavCalendarCollection = (CalDavCalendarCollection)collection;
		
		Calendar[] calendars = caldavCalendarCollection.getCalendars();
		if (calendars == null)
			System.err.println("CalDavCalendar array is null");
		else
			for (int i=0; i < calendars.length; i++)
				dumpCalendar(calendars[i]);
		
		Calendar calendar = collection.getCalendar(uid);
		dumpCalendar(calendar);
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
            }
        }
	}
	
}
