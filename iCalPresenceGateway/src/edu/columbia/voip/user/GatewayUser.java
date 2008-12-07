/**
 * 
 */
package edu.columbia.voip.user;

import java.io.Serializable;

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
}
