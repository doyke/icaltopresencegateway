package edu.columbia.voip.test;

import java.util.Iterator;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
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
		
		//$JUnit-END$
		
		return suite;
	}
	
	public static void dumpCalendar(Calendar calendar)
	{
		System.out.println("\n=-=- DUMPING CALENDAR FIELDS =-=-");
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
