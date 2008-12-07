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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.user.GatewayUser;

/**
 *
 * @author jmoral
 */
public class RegistrationDispatch extends Thread {
    
    private Socket _clientSocket = null;
    private GatewayThread _gatewayThread = null;
    
    /** Creates a new instance of ServerDispatch */
    public RegistrationDispatch(GatewayThread thread, Socket socket) {
    	this._gatewayThread = thread;
        this._clientSocket = socket;
    }
    
    public void run()
    {
        // TODO: handle client connection here
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Got registration connection from: " + _clientSocket.getInetAddress().getHostAddress());
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Begin reading stream for serialized GatewayUser");
        
        // TODO: Read inputsteam from servlet to get GatewayUser
        InputStream in = null;
        try { _clientSocket.getInputStream(); }
        catch (IOException e)	{ Logger.getLogger(getClass().getName()).log(Level.SEVERE, "caught IOException in registration dispatch", e); }
        
        GatewayUser user = null;
        // TODO: Call addGatewayUser on main GatewayThread
        // FIXME: this guy will need a handle to the GatewayThread, passed in on the constructor.
        _gatewayThread.addGatewayUser(user);
    }
    
}
