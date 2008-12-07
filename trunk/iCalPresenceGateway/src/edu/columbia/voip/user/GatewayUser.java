/**
 * 
 */
package edu.columbia.voip.user;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;

import edu.columbia.voip.ical.CalDavConnection;

/**
 * @author jmoral
 *
 */
public class GatewayUser implements Serializable
{
	private static final long serialVersionUID = 1369303190272993151L;
	
	private CalDavConnection _caldavConn = null;
	
	private JabberAccount _jabberAccount = null;
	
	private CalendarAccount _calendarAccount = null;
	
	private Map<String, Date> _lastModifiedMap = null;
	
	/**
	 * @return the _caldavConn
	 */
	public CalDavConnection getCaldavConn() { return _caldavConn; }

	/**
	 * @return the _jabberAccount
	 */
	public JabberAccount getJabberAccount() { return _jabberAccount; }

	/**
	 * @return the _calendarAccount
	 */
	public CalendarAccount getCalendarAccount() { return _calendarAccount; }

	private GatewayUser(String user, char[] pass, String host, String uri, int port, boolean ssl)
	{
		this._lastModifiedMap = new HashMap<String, Date>();
		this._calendarAccount = new CalendarAccount(user, pass, host, uri, port, ssl);
		
		// FIXME: need to be getting actual jabber credentials here.
		this._jabberAccount = new JabberAccount(user, pass, host, port, ssl);
		this._caldavConn = CalDavConnection.createConnection(_calendarAccount);
	}
	
	private GatewayUser(CalendarAccount calAccount, JabberAccount jabAccount)
	{
		this._calendarAccount = calAccount;
		this._jabberAccount = jabAccount;
		this._caldavConn = CalDavConnection.createConnection(_calendarAccount);
	}
	
	public static GatewayUser createUser(String user, char[] pass, String host, String uri, int port, boolean ssl)
	{
		GatewayUser newUser = new GatewayUser(user, pass, host, uri, port, ssl);
		return newUser;
	}

	public static GatewayUser createUser(CalendarAccount calAccount, JabberAccount jabAccount)
	{
		GatewayUser newUser = new GatewayUser(calAccount, jabAccount);
		return newUser;
	}

	/**
	 * Check if given event needs to be passed along to presence server.
	 * @param event
	 * @return true if we need to pass this event to presence, false if presence server is already up to date.
	 * @throws ObjectNotFoundException
	 * @throws ParseException
	 */
	public boolean sendEventToPresence(Calendar event) throws ObjectNotFoundException, ParseException
	{
		boolean shouldSend = false;
		// TODO: Create a new exception in the event that I can't determine if we should
		// 		 pass this calendar event to presence or not.
		Property modifiedProp 	= event.getProperty("LAST-MODIFIED");
		Property uidProp 		= event.getProperty("UID");
		
		if (modifiedProp == null)
			throw new ObjectNotFoundException("Could not get last-modified property from calendar event?");
		if (uidProp == null)
			throw new ObjectNotFoundException("Could not get uid property from calendar event?");
		
		net.fortuna.ical4j.model.Date eventModifiedDate = new net.fortuna.ical4j.model.Date(modifiedProp.getValue());
		long lastModified = eventModifiedDate.getTime();
		String uid = uidProp.getValue();
		
		if (isNewEvent(uid) ||					// either first time we're seeing this event, OR 
			isEventModified(lastModified, uid))	// event was updated since we last saw it.
		{
			// indicate that we need to update presence with this calendar event.
			shouldSend = true;
			updateForEvent(lastModified, uid);
		}
		
		return shouldSend;
	}

	/**
	 * Map for keeping track of my events and their last modified date;
	 * don't want to repeatedly send the same event to the presence server.
	 * Events sent to presence server if modified since last-modified or if
	 * never seen before.
	 * 
	 * String - UID of event
	 * Date - java.util.Date representing last-modified date
	 * 
	 * @return Hashtable of last-modified dates for event of user.
	 */
	public Map<String, Date> getLastModifiedMap() { return _lastModifiedMap; }
	
	private boolean isNewEvent(String uid) 		{ return !(_lastModifiedMap.containsKey(uid)); }
	private long getModifiedTime(String uid) 	{ return _lastModifiedMap.get(uid).getTime(); }
	
	private boolean isEventModified(long lastModified, String uid)
	{
		return getModifiedTime(uid) < lastModified;
	}

	private void updateForEvent(long lastModifiedFromCalendar, String uid)
	{
		_lastModifiedMap.put(uid, new Date(lastModifiedFromCalendar));
	}
}
