package edu.columbia.voip.test;

import java.text.ParseException;
import java.util.Iterator;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for the iCalToPresenceGateway");
		
		//$JUnit-BEGIN$
		
		/* Basic calendar event fetching */
		suite.addTestSuite(iCalConnectorTest.class);
		suite.addTestSuite(iCalSSLConnectorTest.class);
		suite.addTestSuite(iCalDavConnectionTest.class);
		
		//$JUnit-END$
		
		return suite;
	}
	
	public static void dumpCalendar(Calendar calendar)
	{
		System.out.println("\n=-=- DUMPING CALENDAR FIELDS =-=-");
		for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            System.out.println("Component [" + component.getName() + "]");
            Date startDate = null;

            for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                Property property = (Property) j.next();
                System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
                
                try {
	                if (property.getName().equals("DTSTART"))
	                	startDate = new Date(property.getValue());
                }
                catch (ParseException e) { e.printStackTrace(); }
            	
                
                if (property.getName().equals("DURATION"))
                {
                	Dur duration = new Dur(property.getValue());
                	if (duration == null)
                		System.err.println("duration date null!");
                	else
                	{
                		java.util.Date endDate = duration.getTime(startDate);
                		System.err.println("start date:  " + startDate.toGMTString());
                		System.err.println("end date:    " + endDate.toGMTString());
                	}
                }
            }
        }
	}

}
