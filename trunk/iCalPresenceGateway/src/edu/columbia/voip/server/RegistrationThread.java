/*
 * Server.java
 *
 * Created on November 1, 2008, 7:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

import edu.columbia.voip.server.conf.ServerParameters;

/**
 *
 * @author jmoral
 */
public class RegistrationThread implements Runnable {
    
    private ServerSocket _serverSocket = null;
    private GatewayThread _gatewayThread = null;
    
    /**
     * Creates a new instance of Server
     */
    public RegistrationThread(GatewayThread gatewayThread) throws IOException {
    	_gatewayThread = gatewayThread;
        _serverSocket = new ServerSocket(ServerParameters.REGISTRATION_PORT);
    }
    
    public void run()
    {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Starting up registration thread");
        
        while (true)
        {
            try {
                Socket clientSocket = _serverSocket.accept();
                (new RegistrationDispatch(_gatewayThread, clientSocket)).start();
            } catch (IOException e) {
            	Logger.getLogger(getClass().getName()).log(Level.SEVERE, "got exception in Registration thread.", e);
            }
        }
    }
    
}
