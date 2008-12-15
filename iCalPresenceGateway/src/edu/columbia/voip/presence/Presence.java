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
import java.util.Random;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

/**
 *
 * @author jmoral
 */
public class Presence {

    private SipLayer _sipLayer = null;

    /** Cannot be instantiated for now-- only have the one static sendMessage method */
    public Presence(SipLayer _sipLayer) {
    	this._sipLayer = _sipLayer;
    }

    public void sendMessage(String userid, String pidfMsg) {
        Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send SIP presence message to '" +
                userid + "'");
        // TODO @Milind, send this calendar event to the presence server for user <user>
        //String pidfMsg = createPidf(userid, location, summary, description, start.toString(), end.toString(), category);
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

    private static String createTuple(String summary, String description, String location, String startDate, String endDate, String category) {
        Random generator = new Random();
        int r = generator.nextInt();
        String random = Integer.toString(r);



        String cumulativeTuple;

        String tuplePidf = "<tuple id =\"" + random + "\">";
        String statusPidf = "<status><basic>closed</basic>";
        String activityPidf = "<cal:activity>";
        String summaryPidf = "<cal:summary>" + summary + "</cal:summary>";
        cumulativeTuple = tuplePidf + statusPidf + activityPidf + summaryPidf;
        if (!description.equalsIgnoreCase("[no description]")) {
            String descriptionPidf = "<cal:description>" + description + "</cal:description>";
            cumulativeTuple = cumulativeTuple + descriptionPidf;
        }
        if (!location.equalsIgnoreCase("[no location]")) {
            String locationPidf = "<cal:location>" + location + "</cal:location>";
            cumulativeTuple = cumulativeTuple + locationPidf;
        }
        String startDatePidf = "<cal:startdate>" + startDate + "</cal:startdate>";
        cumulativeTuple = cumulativeTuple + startDatePidf;
        String endDatePidf = "<cal:enddate>" + endDate + "</cal:enddate>";
        cumulativeTuple = cumulativeTuple + endDatePidf;
        if (!category.equalsIgnoreCase("[no categories]")) {
            String categoryPidf = "<cal:category>" + category + "</cal:category>";
            cumulativeTuple = cumulativeTuple + categoryPidf;
        }
        String activityclosePidf = "</cal:activity>";
        String statusclosePidf = "</status>";
        String tupleclosePidf = "</tuple>";
        cumulativeTuple = cumulativeTuple + activityclosePidf + statusclosePidf + tupleclosePidf;

        return cumulativeTuple;
    }

    public void sendAvailableMessage(String primaryKey) {
        // TODO Auto-generated method stub
        String presencePidf = null;


        String headerPidf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><presence xmlns=\"urn:ietf:params:xml:ns:pidf\" xmlns:cal=\"http://id.example.com/presence/\"";
        String entityPidf = "entity=\"pres:" + primaryKey + "@columbia.edu\">";
        String endPidf = "</presence>";

        presencePidf =  headerPidf + entityPidf;

        Random generator = new Random();
        int r = generator.nextInt();
        String random = Integer.toString(r);
        
        String tuplePidf = "<tuple id =\"" + random + "\">";
        String statusPidf = "<status><basic>closed</basic>";
        String activityPidf = "<cal:activity>";
        String summaryPidf = "<cal:summary>Available</cal:summary>";
        String activityclosePidf = "</cal:activity>";
        String statusclosePidf = "</status>";
        String tupleclosePidf = "</tuple>";
        
        presencePidf = presencePidf + tuplePidf + statusPidf + activityPidf + summaryPidf + activityclosePidf + statusclosePidf + tupleclosePidf + endPidf;
        
        sendMessage(primaryKey, presencePidf);     
    }

    public void sendMessage(String primaryKey,
            List<PresenceCalendar> presenseCalendars) {
        // TODO Auto-generated method stub
        Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send " + presenseCalendars.size() +
                " SIP presence messages to '" + primaryKey + "'");

        String presencePidf = null;


        String headerPidf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><presence xmlns=\"urn:ietf:params:xml:ns:pidf\" xmlns:cal=\"http://id.example.com/presence/\"";
        String entityPidf = "entity=\"pres:" + primaryKey + "@columbia.edu\">";
        String endPidf = "</presence>";

        presencePidf = presencePidf + headerPidf + entityPidf;

        for (PresenceCalendar cal : presenseCalendars) {

            presencePidf = presencePidf + createTuple(cal.getSummary(), cal.getDescription(), cal.getLocation(), cal.getStarttime(), cal.getEndtime(), cal.getLategory());

        }

        presencePidf = presencePidf + endPidf;

        sendMessage(primaryKey, presencePidf);

    }
}
