/**
 * 
 */
package edu.columbia.voip.test;

import java.util.List;

import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.jackrabbit.webdav.DavException;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.connector.ObjectStoreException;
import net.fortuna.ical4j.model.Calendar;
import edu.columbia.voip.ical.CalDavConnection;
import edu.columbia.voip.ical.NoCalendarEventsException;
import edu.columbia.voip.user.CalendarAccount;
import junit.framework.TestCase;

/**
 * @author jmoral
 *
 */
public class iCalDavConnectionTest extends TestCase {

	/** SSL */
	private static final String _ssl_username = "voipproject1";
	private static final char[] _ssl_password = "funvoip".toCharArray();
	public static final String 	SSL_HOST = "calendar.cs.columbia.edu";
	public static final int 	SSL_PORT = 8443;
	public static final String 	SSL_BASE_STORE_PATH = "/calendars/groups/voipprojectgroup2/calendar/";

	private CalendarAccount _account = null;
	private CalDavConnection _conn = null;
	
	public void setUp() {
		_account = new CalendarAccount(_ssl_username, _ssl_password, SSL_HOST, SSL_BASE_STORE_PATH, SSL_PORT, true); 
	}

	/**
	 * Test method for {@link edu.columbia.voip.ical.CalDavConnection#createConnection(edu.columbia.voip.user.CalendarAccount)}.
	 */
	public void testCreateConnection() {
		_conn = CalDavConnection.createConnection(_account);
		assertNotNull("couldn't create calendar connection?", _conn);
	}
	
	/**
	 * Test method for {@link edu.columbia.voip.ical.CalDavConnection#getCalendars()}.
	 */
	public void testGetCalendars() {
		assertNotNull("couldn't create acount?", _account);
		_conn = CalDavConnection.createConnection(_account);
		assertNotNull("couldn't create calendar connection?", _conn);
		
		try {
			List<Calendar> calendars = _conn.getCalendars();
			assertNotNull("didn't get any calendars but no exception thrown?", calendars);
		}
		catch (ObjectNotFoundException e) 	{ fail("ObjectNotFoundException: " + e.getMessage()); }
		catch (ObjectStoreException e)		{ fail("ObjectStoreException: " + e.getMessage()); }
		catch (NoCalendarEventsException e) { fail("NoCalendarEventsException: " + e.getMessage()); }
	}
}
