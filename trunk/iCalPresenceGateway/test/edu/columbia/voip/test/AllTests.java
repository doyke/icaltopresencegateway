package edu.columbia.voip.test;

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

}
