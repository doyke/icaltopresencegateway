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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.server.conf.ConfParseException;
import edu.columbia.voip.server.conf.ConfProcessor;
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
    		
            _registrationThread = new Thread(new RegistrationThread());
            _registrationThread.start();
            
            _gatewayThread.join();
            _registrationThread.join();
        } 
    	catch (ConfParseException e) 	{ die(e); }
        catch (IOException e)			{ die(e); }
        catch (DatabaseException e)		{ die(e); }
        catch (InterruptedException e)	{ die(e); }
        catch (Exception e)				{ die(e); }
    }

	private void doBootstrap() throws ConfParseException, DatabaseException
	{
		// parse conf file and load configurations into ServerParameters
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Loading configurations from gateway.conf...");
		ConfProcessor.loadConfFile();
		
		// connect to DB and get registrations
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Building MySQL database connection.");
		_dbConnection = DBEngine.buildConnection();
		_userList = _dbConnection.getAllRegisteredUsers();
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
