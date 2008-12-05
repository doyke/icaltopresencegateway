package edu.columbia.voip.ical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.protocol.Protocol;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.connector.ObjectStoreException;
import net.fortuna.ical4j.connector.dav.CalDavCalendarCollection;
import net.fortuna.ical4j.connector.dav.CalDavCalendarStore;
import net.fortuna.ical4j.model.Calendar;

public class CalDavConnection {

	private static final String PRODID = "-//John Morales//iCal4j Connector 1.0//EN";
	
	private CalendarAccount 	_account = null;
	private CalDavCalendarStore _store = null;
	private Protocol 			_protocol = null;
	
	public CalDavConnection(CalendarAccount account)
	{
		_account = account;
		_protocol = (account.isSSLEnabled()) ? Protocol.getProtocol("https") : Protocol.getProtocol("http"); 
		_store = new CalDavCalendarStore(PRODID, account.getHost(), account.getPort(), _protocol, account.getUri());
	}
	
	public List<Calendar> getCalendars(String user, String pass, String URI) 
			throws NoCalendarEventsException, ObjectStoreException, ObjectNotFoundException
	{
		List<Calendar> calendarList = null;
		_store.connect(_account.getUsername(), _account.getPassword().toCharArray());
		
		List<String> icsFiles = _store.getCalendarUidPaths();
		if (icsFiles == null)
			throw new NoCalendarEventsException("user " + _account.getUsername() + " does not have any calendar events");
		
		calendarList = new ArrayList<Calendar>(icsFiles.size());
		for (Iterator<String> iter = icsFiles.iterator(); iter.hasNext(); )
		{
			CalDavCalendarCollection collection = (CalDavCalendarCollection)_store.getCollection(iter.next());
			if (collection == null) 
				throw new ObjectNotFoundException();
			
			Calendar calendar = collection.getCalendar();
			if (calendar == null) 
				throw new ObjectNotFoundException();
			calendarList.add(calendar);
		}			
		_store.disconnect();
		return calendarList;
	}

}
