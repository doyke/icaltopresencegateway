/**
 * 
 */
package edu.columbia.voip.server;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.connector.ObjectStoreException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;

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
	
	private Logger _logger = null;
	
	/**
	 * 
	 */
	public GatewayDispatch(GatewayUser user)
	{
		this._logger = Logger.getLogger(getClass().getName());
		this._user = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		_logger.log(Level.INFO, 
						"Starting dispatch thread for syncing calendar user '" + _user.getCalendarAccount().getUsername() + "'");	

		// TODO: need to do all syncing here
		List<Calendar> myEvents = null;
		try { myEvents = _user.getCaldavConn().getCalendars(); } 
		catch (NoCalendarEventsException e) { 
			_logger.log(Level.WARNING, "calendar user '" + _user.getCalendarAccount().getUsername() + "' has no events. We're done here.");
			return;
		} 
		catch (ObjectStoreException e) {
			_logger.log(Level.SEVERE, "Got ObjectStoreException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
			throw new RuntimeException(e);
		} catch (ObjectNotFoundException e) {
			_logger.log(Level.SEVERE, "Got ObjectNotFoundException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
			throw new RuntimeException(e);
		}
		
		// Compare list of calendars to my hashtable of calendar events and
		// last-modified dates to see if I need to pass the events to presence server
		for (Iterator<Calendar> iter = myEvents.iterator(); iter.hasNext(); )
		{
			Calendar event = iter.next();
			dumpEvent(event);
			
			try {
				if (_user.sendEventToPresence(event))
					parseAndSend(event);
			} catch (ObjectNotFoundException e) { 
				_logger.log(Level.SEVERE, "caught ObjectNotFoundException while checking if presence needs updating", e);
			} catch (ParseException e) {
				_logger.log(Level.SEVERE, "caught ParseException while checking if presence needs updating", e);
			}
		}
		_logger.log(Level.INFO, "Successfully exiting dispatch thread for calendar user '" + _user.getCalendarAccount().getUsername() + "'");	
	}

	private void dumpEvent(Calendar event)
	{
		_logger.log(Level.FINE, "\n=-=- DUMPING CALENDAR FIELDS =-=-");
		for (Iterator i = event.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            _logger.log(Level.FINE, "Component [" + component.getName() + "]");
            String propertyInfo = "";
            for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                Property property = (Property) j.next();
                propertyInfo += "Property [" + property.getName() + ", " + property.getValue() + "]\n";
            }
            _logger.log(Level.FINE, propertyInfo);
        }
	}

	private void parseAndSend(Calendar event) throws ObjectNotFoundException, ParseException
	{
		Map<String, Property> propertyMap = new HashMap<String, Property>();
		Date start = null;
		Date end = null;
		
		Component component = event.getComponent("VEVENT");
		if (component == null)
			throw new ObjectNotFoundException("Could not get VEVENT component from calendar event?");
		
		propertyMap.put("SUMMARY", 			component.getProperty("SUMMARY"));
		propertyMap.put("LAST-MODIFIED", 	component.getProperty("LAST-MODIFIED"));
		propertyMap.put("DESCRIPTION", 		component.getProperty("DESCRIPTION"));
		propertyMap.put("LOCATION", 		component.getProperty("LOCATION"));
		propertyMap.put("CATEGORIES", 		component.getProperty("CATEGORIES"));
		propertyMap.put("DTSTART", 			component.getProperty("DTSTART"));
		propertyMap.put("DTEND", 			component.getProperty("DTEND"));
		propertyMap.put("DURATION",			component.getProperty("DURATION"));
		
		start = new Date( (new net.fortuna.ical4j.model.Date(propertyMap.get("DTSTART").getValue())).getTime() );
		if (propertyMap.get("DTEND") != null)
			end = new Date( (new net.fortuna.ical4j.model.Date(propertyMap.get("DTEND").getValue())).getTime() );
		else if (propertyMap.get("DURATION") != null)
		{
			_logger.log(Level.WARNING, "Calendar event does not have a DTEND property; using duration instead.");
			Dur duration = new Dur(propertyMap.get("DURATION").getValue());
        	end = duration.getTime(start);
		}
		else
			throw new ObjectNotFoundException("No end date nor duration? How can I tell when this event is over?");
		
		// Send SIP presence message
		Presence.sendMessage(	_user.getPrimaryKey(),
								propertyMap.get("SUMMARY") == null ? "[No Summary]" : propertyMap.get("SUMMARY").getValue(), 
								propertyMap.get("DESCRIPTION") == null ? "[No Description]" : propertyMap.get("DESCRIPTION").getValue(),  
								propertyMap.get("LOCATION") == null ? "[No Location]" : propertyMap.get("LOCATION").getValue(), 
								propertyMap.get("CATEGORIES") == null ? "[No Categories]" : propertyMap.get("CATEGORIES").getValue(), 
								start, end);
	}
}
