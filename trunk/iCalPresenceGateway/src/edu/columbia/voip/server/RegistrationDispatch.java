/*
 * ServerDispatch.java
 *
 * Created on November 1, 2008, 7:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.server.conf.ServerParameters;
import edu.columbia.voip.user.GatewayUser;

/**
 *
 * @author jmoral
 */
public class RegistrationDispatch extends Thread {
    
    private Socket _clientSocket = null;
    private GatewayThread _gatewayThread = null;
    
    private Logger _logger = null;
    
    /** Creates a new instance of ServerDispatch */
    public RegistrationDispatch(GatewayThread thread, Socket socket) {
    	this._logger = Logger.getLogger(getClass().getName());
		this._gatewayThread = thread;
        this._clientSocket = socket;
    }
    
    public void run()
    {
        // TODO: handle client connection here
    	_logger.log(Level.INFO, "Got registration connection from: " + _clientSocket.getInetAddress().getHostAddress());
    	_logger.log(Level.INFO, "Begin reading stream for serialized GatewayUser");
    	GatewayUser newUser = null;
    	
        // TODO: Read inputsteam from servlet to get GatewayUser
        try { 
        	ObjectInputStream ois = new ObjectInputStream(_clientSocket.getInputStream());
        	newUser = (GatewayUser)ois.readObject();
        	ois.close();
    	}
        catch (IOException e)	{ _logger.log(Level.SEVERE, "caught IOException in registration dispatch", e); }
        catch (ClassNotFoundException e) { _logger.log(Level.SEVERE, "caught ClassNotFoundException in registration dispatch.", e); }
        
        if (newUser != null)
        	_gatewayThread.addGatewayUser(newUser);
        else
        	_logger.log(Level.SEVERE, "user received from registration servlet is NULL, not adding to gateway thread.");
    }
    
}
