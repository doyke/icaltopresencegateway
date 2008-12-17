package edu.columbia.voip.presence;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import edu.columbia.voip.server.GatewayThread;
import edu.columbia.voip.server.conf.ServerParameters;

public class SipLayer implements SipListener {

   

    private String username;

    private SipStack sipStack;

    private SipFactory sipFactory;

    private AddressFactory addressFactory;

    private HeaderFactory headerFactory;

    private MessageFactory messageFactory;

    private SipProvider sipProvider;
    
    private Thread _mainThread = null;

    /** initialize the SIP stack. */
    public SipLayer(String ip, int port)
	    throws PeerUnavailableException, TransportNotSupportedException,
	    InvalidArgumentException, ObjectInUseException,
	    TooManyListenersException 
    {
    	sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		properties.setProperty("javax.sip.STACK_NAME", "LocationScan");
		properties.setProperty("javax.sip.IP_ADDRESS", ip);
	
		
		properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
		properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "gatewaysip.log");
		properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "gatewaysipdebug.log");
	
		sipStack = sipFactory.createSipStack(properties);
		headerFactory = sipFactory.createHeaderFactory();
		addressFactory = sipFactory.createAddressFactory();
		messageFactory = sipFactory.createMessageFactory();
	
		ListeningPoint tcp = sipStack.createListeningPoint(port, "tcp");
		ListeningPoint udp = sipStack.createListeningPoint(port, "udp");
	
		sipProvider = sipStack.createSipProvider(tcp);
		sipProvider.addSipListener(this);
		sipProvider = sipStack.createSipProvider(udp);
		sipProvider.addSipListener(this);
		
		System.out.println("SIP Stack Intialized");
    }

    /**
     * This method uses the SIP stack to send a message. 
     */
    public void sendMessage(String sender, String to, String message) throws ParseException,
	    InvalidArgumentException, SipException 
    {
        setUsername(sender);
        
		SipURI from = addressFactory.createSipURI(getUsername(), getHost()
			+ ":" + getPort());
		Address fromNameAddress = addressFactory.createAddress(from);
		fromNameAddress.setDisplayName(getUsername());
		FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
			"locationscan");
	
		String username = to.substring(to.indexOf(":") + 1, to.indexOf("@"));
		String address = to.substring(to.indexOf("@") + 1);
	
		SipURI toAddress = addressFactory.createSipURI(username, address);
		Address toNameAddress = addressFactory.createAddress(toAddress);
		toNameAddress.setDisplayName(username);
		ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);
	
		SipURI requestURI = addressFactory.createSipURI(username, address);
		requestURI.setTransportParam("udp");
	
		ArrayList viaHeaders = new ArrayList();
		ViaHeader viaHeader = headerFactory.createViaHeader(getHost(),
			getPort(), "udp", "branch1");
		viaHeaders.add(viaHeader);
	
		CallIdHeader callIdHeader = sipProvider.getNewCallId();
	
		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1, Request.PUBLISH);
	
		MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);
	
		Request request = messageFactory.createRequest(requestURI,
			Request.PUBLISH, callIdHeader, cSeqHeader, fromHeader,
			toHeader, viaHeaders, maxForwards);
	
		SipURI contactURI = addressFactory.createSipURI(getUsername(), getHost());
		contactURI.setPort(getPort());
		Address contactAddress = addressFactory.createAddress(contactURI);
		contactAddress.setDisplayName(getUsername());
		ContactHeader contactHeader = headerFactory
			.createContactHeader(contactAddress);
        
        EventHeader evHeader = null;
        evHeader = headerFactory.createEventHeader("presence"); 
         
        ExpiresHeader expHeader = null;
        expHeader = headerFactory.createExpiresHeader(3600);         
         
        request.addHeader(expHeader);
        request.addHeader(evHeader);
        request.addHeader(contactHeader);

        ContentTypeHeader contentTypeHeader = headerFactory
			.createContentTypeHeader("application", "pidf+xml");
		request.setContent(message, contentTypeHeader);
			        
		//sipProvider.sendRequest(request);
		//ClientTransaction s = new ClientTransaction();
		//SipProvider.getNewClientTransaction(Request)
		ClientTransaction client = sipProvider.getNewClientTransaction(request);
		client.setRetransmitTimer(ServerParameters.PRESENCE_RESPONSE_TIMEOUT);
		client.sendRequest();
	}

    /** This method is called by the SIP stack when a response arrives. */
    public void processResponse(ResponseEvent evt) 
    {
		Response response = evt.getResponse();
		int status = response.getStatusCode();
	        
       // System.out.println("Status code: "+status);
	
		if ((status >= 200) && (status < 300)) { //Success!
		    //messageProcessor.processInfo("--Sent");
	        //    System.out.println("--Sent");
	            System.out.println("SIP Message sent successfully");
		    return;
		}
	
		//messageProcessor.processError("Previous message not sent: " + status);
        System.out.println("Previous message not sent: " + status);
    }

    /** 
     * This method is called by the SIP stack when a new request arrives. 
     */
    public void processRequest(RequestEvent evt) {
		Request req = evt.getRequest();
	        
	        
		String method = req.getMethod();
		if (!method.equals("MESSAGE")) { //bad request type.
		   // messageProcessor.processError("Bad request type: " + method);
	            System.out.println("Bad request type: " + method);
		    return;
		}
	
		FromHeader from = (FromHeader) req.getHeader("From");
		
	 
		Response response = null;
		try { //Reply with OK
		    response = messageFactory.createResponse(200, req);
		    ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
		    toHeader.setTag("888"); //This is mandatory as per the spec.
		    ServerTransaction st = sipProvider.getNewServerTransaction(req);
		    st.sendResponse(response);
		} catch (Throwable e) {
		    e.printStackTrace();
		    
	            System.out.println("Can't send OK reply.");
		}
    }

    /** 
     * This method is called by the SIP stack when there's no answer 
     * to a message. Note that this is treated differently from an error
     * message. 
     */
    public void processTimeout(TimeoutEvent evt) 
    {
    	Logger.getLogger(getClass().getName()).log(Level.SEVERE, "FATAL: SIP server did not process my message.");
    	_mainThread.interrupt();
    }

    /** 
     * This method is called by the SIP stack when there's an asynchronous
     * message transmission error.  
     */
    public void processIOException(IOExceptionEvent evt) {
	
        System.out.println("Previous message not sent: "
		+ "I/O Exception");
    }

    /** 
     * This method is called by the SIP stack when a dialog (session) ends. 
     */
    public void processDialogTerminated(DialogTerminatedEvent evt) {
    }

    /** 
     * This method is called by the SIP stack when a transaction ends. 
     */
    public void processTransactionTerminated(TransactionTerminatedEvent evt) {
    }

    public String getHost() {
	int port = sipProvider.getListeningPoint().getPort();
	String host = sipStack.getIPAddress();
	return host;
    }

    public int getPort() {
	int port = sipProvider.getListeningPoint().getPort();
	return port;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String newUsername) {
	username = newUsername;
    }

	public void setMainThread(Thread thread)
	{
		_mainThread = thread;
	}



}
