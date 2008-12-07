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
import java.io.*;

/**
 *
 * @author jmoral
 */
public class RegistrationThread implements Runnable {
    
    private ServerSocket _serverSocket = null;
    
    /**
     * Creates a new instance of Server
     */
    public RegistrationThread() throws IOException {
        _serverSocket = new ServerSocket(ServerParameters.REGISTRATION_PORT);
    }
    
    public void run()
    {
        Logger.getInstance().println("In server thread");
        while (true)
        {
            try {
                Socket clientSocket = _serverSocket.accept();
                (new ServerDispatch(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
                e.printStackTrace(Logger.getInstance());
            }
        }
    }
    
}
