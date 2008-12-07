/*
 * Presence.java
 *
 * Created on November 1, 2008, 7:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.presence;

import edu.columbia.voip.user.GatewayUser;
import net.fortuna.ical4j.model.Calendar;

/**
 *
 * @author jmoral
 */
public class Presence {
    
    /**
     * Creates a new instance of Presence
     */
    public Presence() {
        // TODO: here we can send messages to presence server
    }

	public static void sendMessage(Calendar event, GatewayUser user)
	{
		// TODO @Milind, send this calendar event to the presence server for user <user>
	}
}
