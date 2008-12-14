/**
 * 
 */
package edu.columbia.voip.user;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;


import com.sun.corba.se.pept.encoding.OutputObject;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import edu.columbia.voip.ical.CalDavConnection;
import edu.columbia.voip.server.conf.ServerParameters;

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

	private String _primaryKey = null;
	
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
	
	/**
	 * Map for keeping track of my events and their last modified date;
	 * don't want to repeatedly send the same event to the presence server.
	 * Events sent to presence server if modified since last-modified or if
	 * never seen before.
	 * 
	 * String - UID of event
	 * Date - java.util.Date representing last-modified date and time
	 * 
	 * @return Hashtable of last-modified dates for event of user.
	 */
	public Map<String, Date> getLastModifiedMap() 	{ return _lastModifiedMap; }
	
	/**
	 * Getting for gateway registration primary Key
	 * @return primary key of this user
	 */
	public String getPrimaryKey() 					{ return _primaryKey; }
	
	
	private GatewayUser(String user, char[] pass, String host, String uri, int port, boolean ssl)
	{
		this._lastModifiedMap = new HashMap<String, Date>();
		this._calendarAccount = new CalendarAccount(user, pass, host, uri, port, ssl);
		
		// TODO: dropping Jabber support
		this._jabberAccount = null;//new JabberAccount(user, pass, host);
		this._caldavConn = CalDavConnection.createConnection(_calendarAccount);
	}
	
	private GatewayUser(String primaryKey, CalendarAccount calAccount, JabberAccount jabAccount)
	{
		this._lastModifiedMap = new HashMap<String, Date>();
		this._primaryKey = primaryKey;
		this._calendarAccount = calAccount;
		this._jabberAccount = jabAccount;
		this._caldavConn = CalDavConnection.createConnection(_calendarAccount);
	}
	
	/**
	 * Creates a new GatewayUser with the given registration DB primary key, iCalendarAccount, and JabberAccount
	 * (Jabber not currently implemented) 
	 * @param primaryKey
	 * @param calAccount
	 * @param jabAccount
	 * @return new GatewayUser
	 */
	public static GatewayUser createUser(String primaryKey, CalendarAccount calAccount, JabberAccount jabAccount)
	{
		GatewayUser newUser = new GatewayUser(primaryKey, calAccount, jabAccount);
		return newUser;
	}
}
