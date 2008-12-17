/*
 * Presence.java
 *
 * Created on November 1, 2008, 7:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.columbia.voip.presence;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.server.conf.ServerParameters;
import edu.columbia.voip.user.GatewayUser;
import java.util.Random;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author jmoral
 */
public class Presence {
	
	private SipLayer _sipLayer = null;

	public static final String XML_VERSION        		= "1.0";
	public static final String XML_ENCODING       		= "UTF-8";
	
	public static final String ELEMENT_OPEN				= "open";
	public static final String ELEMENT_CLOSED			= "closed";

	public static final String TAG_PRESENCE 			= "presence";
	public static final String TAG_XMLNS 	 	 		= "xmlns";
	public static final String TAG_XMLNS_RPID 	 		= "xmlns:rpid";
	public static final String TAG_XMLNS_GP 	 		= "xmlns:gp";
	public static final String TAG_XMLNS_CL 	 		= "xmlns:cl";
	public static final String TAG_ENTITY 	 	 		= "entity";
	
	public static final String TAG_XMLNS_VALUE 	 		= "urn:ietf:params:xml:ns:pidf";
	public static final String TAG_XMLNS_RPID_VALUE 	= "urn:ietf:params:xml:ns:pidf:rpid";
	public static final String TAG_XMLNS_GP_VALUE 	 	= "urn:ietf:params:xml:ns:pidf:geopriv10";
	public static final String TAG_XMLNS_CL_VALUE 	 	= "urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc";
	
	public static final String TAG_ID					= "id";
	public static final String TAG_TUPLE 	 	 		= "tuple";
	public static final String TAG_STATUS				= "status";
	public static final String TAG_BASIC				= "basic";
	public static final String TAG_GP_GEOPRIV			= "gp:geopriv";
	public static final String TAG_GP_LOCATION_INFO		= "gp:location-info";
	public static final String TAG_CL_CIVICADDRESS		= "cl:civicAddress";
	public static final String TAG_CL_LOC				= "cl:LOC";
	
	public static final String TAG_RPID_ACTIVITIES		= "rpid:activities";
	public static final String TAG_FROM					= "from";
	public static final String TAG_UNTIL				= "until";
	public static final String TAG_RPID_NOTE			= "rpid:note";
	public static final String TAG_RPID_OTHER			= "rpid:other";
	
	public static final String TAG_NOTE					= "note";
	
	/** array of predefined activity elements from RFC 4480 on RPID presence extension */
	public static final String[] RFC4480_ACTIVITIES		= { "appointment",
															"away", 
															"breakfast",
															"busy",
															"dinner",
															"holiday",
															"in-transit",
															"looking-for-work",
															"lunch",
															"meal",
															"meeting",
															"on-the-phone",
															"performance",
															"permanent-absence",
															"playing",
															"presentation",
															"shopping",
															"sleeping",
															"spectator",
															"steering",
															"travel",
															"tv",
															"unknown",
															"vacation",
															"working",
															"worship"}; 
	
    
    public Presence(SipLayer _sipLayer) {
    	this._sipLayer = _sipLayer;
    }

    public void sendMessage(String userid, String pidfMsg) throws SIPException {
        Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send SIP presence message to '" +
                userid + "'");
        // TODO @Milind, send this calendar event to the presence server for user <user>
        //String pidfMsg = createPidf(userid, location, summary, description, start.toString(), end.toString(), category);
        try {
            _sipLayer.sendMessage(userid, "sip:presence@" + ServerParameters.PRESENCE_HOSTNAME + ":" + 
            												ServerParameters.PRESENCE_PORT, pidfMsg);
        } catch (ParseException ex) {
            Logger.getLogger(Presence.class.getName()).log(Level.SEVERE, "got ParseException from sipLayer", ex);
            throw new SIPException(ex);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(Presence.class.getName()).log(Level.SEVERE, "got InvalidArgumentException from sipLayer", ex);
            throw new SIPException(ex);
        } catch (SipException ex) {
            Logger.getLogger(Presence.class.getName()).log(Level.SEVERE, "got SipException from sipLayer", ex);
            throw new SIPException(ex);
        }
    }

    @Deprecated
    private String createTuple(String summary, String description, String location, String startDate, String endDate, String category) {
        Random generator = new Random();
        int r = generator.nextInt(Integer.MAX_VALUE);
        String random = Integer.toString(r);

        String cumulativeTuple;

        String tuplePidf = "<tuple id =\"" + random + "\">\n";
        String statusPidf = "<status>\n<basic>closed</basic>\n";
        String activityPidf = "<cal:activity>\n";
        String summaryPidf = "<cal:summary>" + summary + "</cal:summary>\n";
        cumulativeTuple = tuplePidf + statusPidf + activityPidf + summaryPidf;
        if (!description.equalsIgnoreCase("[no description]")) {
            String descriptionPidf = "<cal:description>" + description + "</cal:description>\n";
            cumulativeTuple = cumulativeTuple + descriptionPidf;
        }
        if (!location.equalsIgnoreCase("[no location]")) {
            String locationPidf = "<cal:location>" + location + "</cal:location>\n";
            cumulativeTuple = cumulativeTuple + locationPidf;
        }
        String startDatePidf = "<cal:startdate>" + startDate + "</cal:startdate>\n";
        cumulativeTuple = cumulativeTuple + startDatePidf;
        String endDatePidf = "<cal:enddate>" + endDate + "</cal:enddate>\n";
        cumulativeTuple = cumulativeTuple + endDatePidf;
        if (!category.equalsIgnoreCase("[no categories]")) {
            String categoryPidf = "<cal:category>" + category + "</cal:category>\n";
            cumulativeTuple = cumulativeTuple + categoryPidf;
        }
        String activityclosePidf = "</cal:activity>\n";
        String statusclosePidf = "</status>\n";
        String tupleclosePidf = "</tuple>\n";
        cumulativeTuple = cumulativeTuple + activityclosePidf + statusclosePidf + tupleclosePidf;

        return cumulativeTuple;
    }

    public void sendAvailableMessage(String primaryKey) throws SIPException {
        // TODO Auto-generated method stub
        String presencePidf = null;


        String headerPidf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<presence xmlns=\"urn:ietf:params:xml:ns:pidf\" xmlns:cal=\"http://id.example.com/presence/\"";
        String entityPidf = " entity=\"pres:" + primaryKey + "@columbia.edu\">\n";
        String endPidf = "</presence>";

        presencePidf =  headerPidf + entityPidf;

        Random generator = new Random();
        int r = generator.nextInt();
        String random = Integer.toString(r);
        
        String tuplePidf = "<tuple id =\"" + random + "\">\n";
        String statusPidf = "<status>\n<basic>closed</basic>\n";
        String activityPidf = "<cal:activity>\n";
        String summaryPidf = "<cal:summary>Available</cal:summary>\n";
        String activityclosePidf = "</cal:activity>\n";
        String statusclosePidf = "</status>\n";
        String tupleclosePidf = "</tuple>\n";
        
        presencePidf = 	presencePidf + tuplePidf + statusPidf + activityPidf + summaryPidf + 
        				activityclosePidf + statusclosePidf + tupleclosePidf + endPidf;
        
        sendMessage(primaryKey, presencePidf);     
    }
    
    @Deprecated
    public void sendMessage(String primaryKey,
            List<PresenceCalendar> presenseCalendars) throws SIPException {
        // TODO Auto-generated method stub
        Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send " + presenseCalendars.size() +
                " SIP presence messages to '" + primaryKey + "'");

        String presencePidf = null;


        String headerPidf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<presence xmlns=\"urn:ietf:params:xml:ns:pidf\" xmlns:cal=\"http://id.example.com/presence/\"";
        String entityPidf = " entity=\"pres:" + primaryKey + "@columbia.edu\">\n";
        String endPidf = "</presence>";

        presencePidf = headerPidf + entityPidf;

        for (PresenceCalendar cal : presenseCalendars) {

            presencePidf = presencePidf + createTuple(cal.getSummary(), cal.getDescription(), cal.getLocation(), cal.getStarttime(), cal.getEndtime(), cal.getLategory());

        }

        presencePidf = presencePidf + endPidf;

        sendMessage(primaryKey, presencePidf);

    }
    
    public void sendMessage(GatewayUser user,
			List<PresenceCalendar> presenseCalendars) throws SIPException
	{
    	Logger.getLogger(Presence.class.getName()).log(Level.INFO, "Got request to send " + presenseCalendars.size() +
                " SIP presence messages to '" + user.getPrimaryKey() + "'");
        String pidfMessage = null;
    	try { 
			pidfMessage = createEventRPIDBody(user, presenseCalendars);
			Logger.getLogger(getClass().getName()).log(Level.FINE, pidfMessage);
    		sendMessage(user.getPrimaryKey(), pidfMessage);
    	}
    	catch (IOException e) { throw new SIPException(e); }
		catch (ParserConfigurationException e) { throw new SIPException(e); }
	}

    /**
     * Creates XML body for SIP presence message based on list of in-progress calendar event fields.
     * @throws IOException
     * @throws ParserConfigurationException 
     */
    private String createEventRPIDBody(GatewayUser user, List<PresenceCalendar> events) throws IOException, ParserConfigurationException
    {	
    	Element root = null;
        Document xmlDoc = null;
        
        Random generator = new Random();
        int r = generator.nextInt(Integer.MAX_VALUE);
        String random = Integer.toString(r);

    	// Create an XML Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        xmlDoc = docBuilder.newDocument();
    
        // Create the root element with namespace attributes
        root = xmlDoc.createElement(TAG_PRESENCE);
        root.setAttribute(TAG_XMLNS, TAG_XMLNS_VALUE);
        root.setAttribute(TAG_XMLNS_RPID, TAG_XMLNS_RPID_VALUE);
        root.setAttribute(TAG_XMLNS_GP, TAG_XMLNS_GP_VALUE);
        root.setAttribute(TAG_XMLNS_CL, TAG_XMLNS_CL_VALUE);
        root.setAttribute(TAG_ENTITY, getPresentityName(user.getPrimaryKey(), user.getCalendarAccount().getHost()));

        for (PresenceCalendar event : events)
        {
            Element tuple = xmlDoc.createElement(TAG_TUPLE);
            tuple.setAttribute(TAG_ID, random);
            
            Element status = xmlDoc.createElement(TAG_STATUS);
            
            Element basic = xmlDoc.createElement(TAG_BASIC);
            status.appendChild(basic);
            basic.appendChild(xmlDoc.createTextNode(ELEMENT_CLOSED));
            
            Element geopriv = xmlDoc.createElement(TAG_GP_GEOPRIV);
            status.appendChild(geopriv);
            
            Element locationInfo = xmlDoc.createElement(TAG_GP_LOCATION_INFO);
            geopriv.appendChild(locationInfo);
            Element civicAddress = xmlDoc.createElement(TAG_CL_CIVICADDRESS);
            locationInfo.appendChild(civicAddress);
            Element civicLocation = xmlDoc.createElement(TAG_CL_LOC);
            civicLocation.appendChild(xmlDoc.createTextNode(event.getLocation()));
            civicAddress.appendChild(civicLocation);
            
            Element activites = xmlDoc.createElement(TAG_RPID_ACTIVITIES);
            activites.setAttribute(TAG_FROM, event.getStarttime());
            activites.setAttribute(TAG_UNTIL, event.getEndtime());
            
            Element rpidNote = xmlDoc.createElement(TAG_RPID_NOTE);
            rpidNote.appendChild(xmlDoc.createTextNode(event.getSummary()));
            
            if (isPredefinedActivity(event.getLategory()))
            	activites.appendChild(xmlDoc.createTextNode("rpid:" + event.getLategory()));
            else
            {
            	Element other = xmlDoc.createElement(TAG_RPID_OTHER);
            	other.appendChild(xmlDoc.createTextNode(event.getLategory()));
            	activites.appendChild(other);
            }
            
            Element note = xmlDoc.createElement(TAG_NOTE);
            note.appendChild(xmlDoc.createTextNode(event.getDescription()));
            
            tuple.appendChild(status);
            tuple.appendChild(activites);
            
            root.appendChild(tuple);
        }

        // Add the presence element to the document
        xmlDoc.appendChild(root);
        
       return getXMLString(xmlDoc);
    }
    
    private boolean isPredefinedActivity(String category)
	{
    	for (String predefined : RFC4480_ACTIVITIES)
    		if (predefined.equalsIgnoreCase(category))
    			return true;
    	
    	return false;
	}

	private String getPresentityName(String user, String host)
    {
    	return "pres:" + user + "@" + host;
    }


    /**
     * Returns a DOM XML Document as a String.
     * @param xmlDoc Root node of XML document from which to create a String
     * @return String representation of xml Document
     * @throws IOException 
     * @throws Exception
     */
    private String getXMLString(Document xmlDoc) throws IOException
	{
    	StringWriter  strWriter    	= new StringWriter();
	    XMLSerializer serializer	= new XMLSerializer();
	    OutputFormat  outFormat    	= new OutputFormat();

    	// Setup the format for the XML file
    	outFormat.setEncoding(XML_ENCODING);
    	outFormat.setVersion(XML_VERSION);
    	outFormat.setIndenting(false);
    	//outFormat.setIndent(4);

    	// Define a Writer and apply format settings
    	serializer.setOutputCharStream(strWriter);
    	serializer.setOutputFormat(outFormat);
    	serializer.serialize(xmlDoc);
    	
    	String xmlString = strWriter.toString();
    	strWriter.close();
    	
    	return xmlString;
	}
}
