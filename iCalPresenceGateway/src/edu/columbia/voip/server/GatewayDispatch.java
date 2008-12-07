/**
 * 
 */
package edu.columbia.voip.server;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.connector.ObjectStoreException;
import net.fortuna.ical4j.model.Calendar;

import edu.columbia.voip.ical.NoCalendarEventsException;
import edu.columbia.voip.presence.Presence;
import edu.columbia.voip.user.GatewayUser;

/**
 * @author jmoral
 *
 */
public class GatewayDispatch implements Runnable
{

	private GatewayUser _user = null;
	
	/**
	 * 
	 */
	public GatewayDispatch(GatewayUser user)
	{
		this._user = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		Logger.getLogger(getClass().getName()).log(Level.FINE, 
						"Starting dispatch thread for syncing calendar user '" + _user.getCalendarAccount().getUsername() + "'");	

		// TODO: need to do all syncing here
		List<Calendar> myEvents = null;
		try { myEvents = _user.getCaldavConn().getCalendars(); } 
		catch (NoCalendarEventsException e) { 
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "calendar user '" + _user.getCalendarAccount().getUsername() + "' has no events. We're done here.");
			return;
		} 
		catch (ObjectStoreException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Got ObjectStoreException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
			throw new RuntimeException(e);
		} catch (ObjectNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Got ObjectStoreException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
			throw new RuntimeException(e);
		}
		
		// TODO: compare list of calendars to my hashtable of calendar events and
		//		 last-modified dates to see if I need to pass the events to presence server
		for (Iterator<Calendar> iter = myEvents.iterator(); iter.hasNext(); )
		{
			Calendar event = iter.next();
			try {
				if (_user.sendEventToPresence(event))
					Presence.sendMessage(event, _user);
			} catch (ObjectNotFoundException e) { 
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
								"caught ObjectNotFoundException while checking if presence needs updating", e);
			} catch (ParseException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
								"caught ParseException while checking if presence needs updating", e);
			}
		}
		Logger.getLogger(getClass().getName()).log(Level.FINE, 
				"Successfully exiting dispatch thread for calendar user '" + _user.getCalendarAccount().getUsername() + "'");	
	}

}
