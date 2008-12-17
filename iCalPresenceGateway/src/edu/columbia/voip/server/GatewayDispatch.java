/**
 * 
 */
package edu.columbia.voip.server;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpException;
import org.apache.jackrabbit.webdav.DavException;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.connector.ObjectStoreException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;

import edu.columbia.voip.ical.NoCalendarEventsException;
import edu.columbia.voip.presence.Presence;
import edu.columbia.voip.presence.PresenceCalendar;
import edu.columbia.voip.presence.SIPException;
import edu.columbia.voip.user.GatewayUser;

/**
 * @author jmoral
 *
 */
public class GatewayDispatch implements Runnable
{

	private GatewayUser _user = null;
	
	private Logger _logger = null;
	
	private Presence _presence = null;
	
	/**
	 * 
	 */
	public GatewayDispatch(GatewayUser user, Presence presence)
	{
		this._presence = presence;
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
		List<Calendar> myActiveEvents = new ArrayList<Calendar>();
		
		try { myEvents = _user.getCaldavConn().getCalendars(); } 
		catch (NoCalendarEventsException e) { 
			_logger.log(Level.WARNING, "calendar user '" + _user.getCalendarAccount().getUsername() + "' has no events. We're done here.");
			return;
		} catch (ObjectStoreException e) {
			_logger.log(Level.SEVERE, "Got ObjectStoreException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
		} catch (ObjectNotFoundException e) {
			_logger.log(Level.SEVERE, "Got ObjectNotFoundException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
		} catch (HttpException e) {
			_logger.log(Level.SEVERE, "Got HttpException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
		} catch (IOException e) {
			if (e instanceof NoRouteToHostException)
				_logger.log(Level.SEVERE, getBadErrorString("Very bad, NoRouteToHostException. Do we have a network connection?"), e);
			else if (e instanceof UnknownHostException)
				_logger.log(Level.SEVERE, getBadErrorString("Very bad, UnknownHostException. Is DNS down? Do we have a network connection?"), e);
			else
				_logger.log(Level.SEVERE, "Got IOException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
		} catch (DavException e) {
			_logger.log(Level.SEVERE, "Got DavException from user '" + _user.getCalendarAccount().getUsername() + "'", e);
		} 
		
		// Compare list of calendars to my hashtable of calendar events and
		// last-modified dates to see if I need to pass the events to presence server
		for (Iterator<Calendar> iter = myEvents.iterator(); iter.hasNext(); )
		{
			Calendar event = iter.next();
			dumpEvent(event);
			
			try {
				if (isEventInProgress(event))
					myActiveEvents.add(event);
			} catch (ObjectNotFoundException e) { 
				_logger.log(Level.SEVERE, "caught ObjectNotFoundException while checking if presence needs updating", e);
			} catch (ParseException e) {
				_logger.log(Level.SEVERE, "caught ParseException while checking if presence needs updating", e);
			}
		}
		
		processCalendarEvents(myActiveEvents);
		_logger.log(Level.INFO, "Successfully exiting dispatch thread for calendar user '" + _user.getCalendarAccount().getUsername() + "'");	
	}
	
	private String getBadErrorString(String message)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("\n\n");
		buf.append("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n");
		buf.append(message + "\n");
		buf.append("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n");
		return buf.toString();
	}

	private void processCalendarEvents(List<Calendar> myActiveEvents)
	{
		if (myActiveEvents.isEmpty())
		{
			if (_user.getPreviousActiveEvents().isEmpty())
			{
				_logger.log(Level.INFO, "Nothing to do");
				return;
			}
			
			// if any events are in my hashtable then those events have now
			// ended. send a available presence message.
			try { _presence.sendAvailableMessage(_user); }
			catch (SIPException e) { _logger.log(Level.SEVERE, "got SIP exception when trying to send available message."); }
			_user.getLastModifiedMap().clear();
			_user.getPreviousActiveEvents().clear();
			
		}
		else
		{
			if (isChangeFrom(myActiveEvents))
			{
				try {
					syncChangedLists(myActiveEvents);
					
					StringBuffer summaryBuf = new StringBuffer("Sending the following calendar events:\n");
					for (Calendar event : myActiveEvents)
					{
						Component component = event.getComponent(Component.VEVENT);
						summaryBuf.append(component.getProperty(Property.SUMMARY).getValue() + "\n");
					}
					_logger.log(Level.INFO, summaryBuf.toString());
					
					parseAndSend(myActiveEvents); 
				} catch (ObjectNotFoundException e) {
					_logger.log(Level.SEVERE, "caught ObjectNotFoundException while syncing event state", e);
				} catch (ParseException e) {
					_logger.log(Level.SEVERE, "caught ParseException while syncing event state", e);
				} catch (SIPException e) {
					_logger.log(Level.SEVERE, "caught SIPException message while sending to presence", e);
				}
			}
		}
	}

	private void syncChangedLists(List<Calendar> myActiveEvents) throws ObjectNotFoundException, ParseException
	{
		_user.getPreviousActiveEvents().clear();
		_user.getPreviousActiveEvents().addAll(myActiveEvents);
		
		_user.getLastModifiedMap().clear();
		for (Calendar event : myActiveEvents)
		{
			String eventHashUid = null;
			Component component = event.getComponent(Component.VEVENT);
			
			Property propUid 		= component.getProperty(Property.UID);
			
			Property propModified 	= component.getProperty(Property.LAST_MODIFIED);
			Property propCreated	= component.getProperty(Property.CREATED);
			Property propStamp		= component.getProperty(Property.DTSTAMP);
			
			if (propUid == null)
				throw new ObjectNotFoundException("Could not get uid property from calendar event?");
			if (propModified == null && propCreated == null && propStamp == null)
				throw new ObjectNotFoundException("Could not get any time property from calendar event?");
			
			// need to use DateTime to get necessary time precision
			net.fortuna.ical4j.model.DateTime eventHashDate = null;
			eventHashUid = propUid.getValue();
			
			// first consider last-modified date, then created date, then finally timestamp date
			if (propModified != null)
				eventHashDate = new net.fortuna.ical4j.model.DateTime(propModified.getValue());
			else if (propCreated != null)
				eventHashDate = new net.fortuna.ical4j.model.DateTime(propCreated.getValue());
			else if (propStamp != null)
				eventHashDate = new net.fortuna.ical4j.model.DateTime(propStamp.getValue());
			
			putModifiedDate(eventHashUid, eventHashDate);
		}
	}

	private boolean isChangeFrom(List<Calendar> myActiveEvents)
	{
		if (myActiveEvents.size() != _user.getPreviousActiveEvents().size())
			return true;
		
		Collection<Calendar> previousList = _user.getPreviousActiveEvents();
		
		// essentially doing a list1.equals(list2) by *value* instead of by reference.
		if (previousList.containsAll(myActiveEvents) && myActiveEvents.containsAll(previousList))
			return false; // the two lists have all the exact same elements.
		else
			return true;
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
	
	private boolean isEventInProgress(Calendar event) throws ParseException, ObjectNotFoundException
	{
		Component component = event.getComponent(Component.VEVENT);
		Date start = getEventStartDate(component.getProperty(Property.DTSTART));
		Date end = getEventEndDate(component.getProperty("DTEND"), component.getProperty(Property.DURATION), start);
		
		if (isEventActive(start, end))
		{
			_logger.log(Level.INFO, "Event '" + component.getProperty(Property.SUMMARY).getValue() + "' IS currently happening.");
			return true;
		}
		else
		{
			_logger.log(Level.INFO, "Event '" + component.getProperty(Property.SUMMARY).getValue() + "' is not currently happening.");
			return false;
		}
	}
	
	
	/**
	 * Check if given event needs to be passed along to presence server.
	 * @param event
	 * @return true if we need to pass this event to presence, false if presence server is already up to date.
	 * @throws ObjectNotFoundException
	 * @throws ParseException
	 */
	@Deprecated
	private boolean isEventNewOrModified(Calendar event) throws ObjectNotFoundException, ParseException
	{
		// TODO: Create a new exception in the event that I can't determine if we should
		// 		 pass this calendar event to presence or not.
		boolean shouldSend = false;
		String eventHashUid = null;
		
		Component component = event.getComponent(Component.VEVENT);
		if (component == null)
			throw new ObjectNotFoundException("Could not get VEVENT component from calendar event?");
		
		Property propModified 	= component.getProperty(Property.LAST_MODIFIED);
		Property propUid 		= component.getProperty(Property.UID);
		Property propCreated	= component.getProperty(Property.CREATED);
		Property propStamp		= component.getProperty(Property.DTSTAMP);
		
		if (propUid == null)
			throw new ObjectNotFoundException("Could not get uid property from calendar event?");
		if (propModified == null && propCreated == null && propStamp == null)
			throw new ObjectNotFoundException("Could not get any time property from calendar event?");
		
		// need to use DateTime to get necessary time precision
		net.fortuna.ical4j.model.DateTime eventHashDate = null;
		eventHashUid = propUid.getValue();
		
		// first consider last-modified date, then created date, then finally timestamp date
		if (propModified != null)
			eventHashDate = new net.fortuna.ical4j.model.DateTime(propModified.getValue());
		else if (propCreated != null)
			eventHashDate = new net.fortuna.ical4j.model.DateTime(propCreated.getValue());
		else if (propStamp != null)
			eventHashDate = new net.fortuna.ical4j.model.DateTime(propStamp.getValue());
		
		if (isNewEvent(eventHashUid) ||						// either first time we're seeing this event, OR 
			isEventModified(eventHashDate, eventHashUid))	// event was updated since we last saw it.
		{
			// indicate that we need to update presence with this calendar event.
			shouldSend = true;
			putModifiedDate(eventHashUid, eventHashDate);
		}
		
		Logger.getLogger(getClass().getName()).log(Level.FINE, (shouldSend ? "" : "NOT ") + "doing send for SIP message uid: " + eventHashUid);
		
		return shouldSend;
	}
	
	private boolean isEventActive(Date start, Date end)
	{
		Date now = new Date();
		return (now.after(start) && now.before(end)); 
	}

	private void parseAndSend(List<Calendar> events) throws ObjectNotFoundException, ParseException, SIPException
	{
		Map<String, Property> propertyMap = new HashMap<String, Property>();
		Date start = null;
		Date end = null;
		List<PresenceCalendar> presenseCalendars = new ArrayList<PresenceCalendar>();
		
		for (Calendar event : events)
		{
			Component component = event.getComponent(Component.VEVENT);
			if (component == null)
				throw new ObjectNotFoundException("Could not get VEVENT component from calendar event?");
			
			propertyMap.put(Property.SUMMARY, 			component.getProperty(Property.SUMMARY));
			propertyMap.put(Property.LAST_MODIFIED, 	component.getProperty(Property.LAST_MODIFIED));
			propertyMap.put(Property.DESCRIPTION, 		component.getProperty(Property.DESCRIPTION));
			propertyMap.put(Property.LOCATION, 			component.getProperty(Property.LOCATION));
			propertyMap.put(Property.CATEGORIES, 		component.getProperty(Property.CATEGORIES));
			propertyMap.put(Property.DTSTART, 			component.getProperty(Property.DTSTART));
			propertyMap.put(Property.DTEND, 			component.getProperty(Property.DTEND));
			propertyMap.put(Property.DURATION,			component.getProperty(Property.DURATION));
			
			start = getEventStartDate(propertyMap.get(Property.DTSTART));
			end = getEventEndDate(propertyMap.get(Property.DTEND), propertyMap.get(Property.DURATION), start);
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String tz = (new SimpleDateFormat("Z")).format(start);
			tz = tz.substring(0,3) + ":" + tz.substring(3);
			
			presenseCalendars.add(new PresenceCalendar( getPropertyString(propertyMap.get(Property.SUMMARY), Property.SUMMARY), 
														getPropertyString(propertyMap.get(Property.DESCRIPTION), Property.DESCRIPTION),  
														getPropertyString(propertyMap.get(Property.LOCATION), Property.LOCATION),  
														getPropertyString(propertyMap.get(Property.CATEGORIES), Property.CATEGORIES),  
														df.format(start) + tz,
														df.format(end) + tz));
		}
		
		// Send SIP presence message
		_presence.sendMessage(_user, presenseCalendars);
	}
	
	private String getPropertyString(Property property, String name)
	{
		if (property == null)
			return "";
		else
			return property.getValue();
	}

	/**
	 * Parse properties to get end Date (java.util). First tries to get DTEND, and if
	 * that doesn't exist then adds DURATION to start date.
	 * @param propEnd
	 * @param propDuration
	 * @param start
	 * @return java.util.Date of event's ending time.
	 * @throws ParseException
	 * @throws ObjectNotFoundException
	 */
	private Date getEventEndDate(Property propEnd, Property propDuration, Date start) 
		throws ParseException, ObjectNotFoundException
	{
		Date end = null;
		// same thing as getEventStartDate below-- try using datetime first, then use date if necessary. 
		if (propEnd != null)
		{
			try { end = new Date( (new net.fortuna.ical4j.model.DateTime(propEnd.getValue())).getTime() ); }
			catch (ParseException e) {
				end = new Date( (new net.fortuna.ical4j.model.Date(propEnd.getValue())).getTime() );
			}
		}
		else if (propDuration != null)
		{
			_logger.log(Level.WARNING, "Calendar event does not have a DTEND property; using duration instead.");
			Dur duration = new Dur(propDuration.getValue());
        	end = duration.getTime(start);
		}
		else
			throw new ObjectNotFoundException("No end date nor duration? How can I tell when this event is over?");
		return end;
	}

	/**
	 * Parse properties to get start Date (java.util) from DTSTART.
	 * @param property
	 * @return
	 * @throws ParseException
	 */
	private Date getEventStartDate(Property property)
			throws ParseException
	{
		Date start = null;
		// try using datetime first to get necessary precision. if it throws a ParseException, 
		// it's probably because it's an all day event so try again using date instead of datetime.
		try { start = new Date( (new net.fortuna.ical4j.model.DateTime(property.getValue())).getTime() ); }
		catch (ParseException e) {
			start = new Date( (new net.fortuna.ical4j.model.Date(property.getValue())).getTime() );
		}
		return start;
	}
	
	private boolean isNewEvent(String uid) 							{ return !(_user.getLastModifiedMap().containsKey(uid)); }
	private void putModifiedDate(String uid, Date eventHashDate) 	{ _user.getLastModifiedMap().put(uid, eventHashDate); }
	private Date getModifiedDate(String uid) 						{ return _user.getLastModifiedMap().get(uid); }
	
	private boolean isEventModified(Date lastModified, String uid)
	{
		return getModifiedDate(uid).compareTo(lastModified) < 0;
	}
}
