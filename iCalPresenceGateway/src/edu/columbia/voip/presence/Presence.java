/*
 * Presence.java
 *
 * Created on November 1, 2008, 7:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.presence;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.user.GatewayUser;


/**
 *
 * @author jmoral
 */
public class Presence {
	
	private static SipLayer _sipLayer = null;
    
    /** Cannot be instantiated for now-- only have the one static sendMessage method */
    private Presence() {
    }

	public static void sendMessage(String userid, String summary, String description,
			String location, String category, Date start, Date end)
	{
		Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send SIP presence message to calendar event");
		// TODO @Milind, send this calendar event to the presence server for user <user>
	}

	public static void setSipLayer(SipLayer _sipLayer)
	{
		Presence._sipLayer = _sipLayer;
	}
}
