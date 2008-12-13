/*
 * Main.java
 *
 * Created on November 1, 2008, 6:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.TransportNotSupportedException;

import edu.columbia.voip.presence.Presence;
import edu.columbia.voip.presence.SIPException;
import edu.columbia.voip.presence.SipLayer;
import edu.columbia.voip.server.conf.ConfParseException;
import edu.columbia.voip.server.conf.ConfProcessor;
import edu.columbia.voip.server.conf.ServerParameters;
import edu.columbia.voip.user.GatewayUser;

/**
 *
 * @author jmoral
 */
public class Main 
{
    private static Thread _registrationThread = null;
    private static GatewayThread _gatewayThread = null;
    
    private static DBEngine _dbConnection = null;
    private static List<GatewayUser> _userList = null;
    
    private void die(Exception e)
    {
    	Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception caught", e);
    	System.err.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    	System.err.println("A fatal error has occurred. The System will now shutdown");
    	System.err.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    	
    	// close open files and connections
    	try { doCleanup(); }
    	catch (DatabaseException ex) { 
    		Logger.getLogger(getClass().getName()).log(Level.SEVERE, "can't even close without error.", ex); 
		}
    	
    	System.exit(1);
    }
    
    private void doCleanup() throws DatabaseException
	{
		// TODO: probably need to close other connections here.
    	Logger.getLogger(getClass().getName()).log(Level.INFO, ServerParameters.getParametersString());
    	if (_dbConnection != null)
    		_dbConnection.closeConnection();
	}

    public Main()
    {
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting up Gateway: " + new Date());
		
    	try {
    		doBootstrap();
        	
    		_gatewayThread = new GatewayThread(_userList);
    		_gatewayThread.start();
    		
            _registrationThread = new Thread(new RegistrationThread(_gatewayThread));
            _registrationThread.start();
            
            _gatewayThread.join();
            _registrationThread.join();
        } 
    	catch (ConfParseException e) 	{ die(e); }
    	catch (DatabaseException e)		{ die(e); }
        catch (IOException e)			{ die(e); }
        catch (InterruptedException e)	{ die(e); }
        catch (Exception e)				{ die(e); }
    }

	private void doBootstrap() throws ConfParseException, DatabaseException, SIPException
	{
		// parse conf file and load configurations into ServerParameters
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Loading configurations from gateway.conf...");
		ConfProcessor.loadConfFile();
		
		// initialized sip stack
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Initializing SIP Layer...");
		SipLayer sipLayer = null;
		try { sipLayer = new SipLayer(InetAddress.getLocalHost().getHostAddress(), 5061); }
		catch (TooManyListenersException e) 	{ throw new SIPException(e); }
		catch (ObjectInUseException e) 			{ throw new SIPException(e); }
		catch (UnknownHostException e) 			{ throw new SIPException(e); }
		catch (InvalidArgumentException e) 		{ throw new SIPException(e); }
		catch (TransportNotSupportedException e){ throw new SIPException(e); }
		catch (PeerUnavailableException e) 		{ throw new SIPException(e); }
		Presence.setSipLayer(sipLayer);
		
		// connect to DB and get registrations
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Building MySQL database connection.");
		_dbConnection = DBEngine.buildConnection();
		_userList = _dbConnection.getAllRegisteredUsers();
		
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Bootstrap done, setting up logger to start writing to file...");
		Logger.getLogger("").addHandler(ServerParameters.FILEHANDLER);
		Logger.getLogger("").setLevel(ServerParameters.LOG_LEVEL);
	}

	/**
     * And.... they're off!
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new Main();
    }
}
