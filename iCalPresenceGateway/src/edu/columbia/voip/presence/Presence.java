/*
 * Presence.java
 *
 * Created on November 1, 2008, 7:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.columbia.voip.presence;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.user.GatewayUser;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

/**
 *
 * @author jmoral
 */
public class Presence {

    private static SipLayer _sipLayer = null;

    /** Cannot be instantiated for now-- only have the one static sendMessage method */
    private Presence() {
    }

    @Deprecated
    public static void sendMessage(String userid, String summary, String description,
            String location, String category, Date start, Date end) {
        Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send SIP presence message to '" + 
        												userid + "' with summary " + summary);
        // TODO @Milind, send this calendar event to the presence server for user <user>
        String pidfMsg = createPidf(userid, location, summary, description, start.toString(), end.toString(), category);
        try {
            _sipLayer.sendMessage(userid, "sip:presence@128.59.18.182:5060", pidfMsg);
        } catch (ParseException ex) {
            Logger.getLogger(Presence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(Presence.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SipException ex) {
            Logger.getLogger(Presence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setSipLayer(SipLayer _sipLayer) {
        Presence._sipLayer = _sipLayer;
    }

    private static String createPidf(String userid, String location, String summary, String description, String startDate, String endDate, String category) {
        
        String pidf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><presence xmlns=\"urn:ietf:params:xml:ns:pidf\" xmlns:local=\"urn:example-com:pidf-status-type\" entity=\"pres:"+userid+"@columbia.edu\"><tuple id=\"ub93s3\"><status><basic>closed</basic><local:location>"+location+"</local:location></status></tuple><note>"+summary+";"+description+";"+startDate+";"+endDate+";"+category+"</note>></presence>";
        
        return pidf;
    }

	public static void sendAvailableMessage(String primaryKey)
	{
		// TODO Auto-generated method stub
		
	}

	public static void sendMessage(String primaryKey,
			List<PresenceCalendar> presenseCalendars)
	{
		// TODO Auto-generated method stub
		Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send " + presenseCalendars.size() + 
										" SIP presence messages to '" + primaryKey + "'");

	}
}
