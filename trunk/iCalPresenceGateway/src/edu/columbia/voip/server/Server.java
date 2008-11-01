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
public class Server implements Runnable {
    
    private ServerSocket _serverSocket = null;
    
    /**
     * Creates a new instance of Server
     */
    public Server() throws IOException {
        _serverSocket = new ServerSocket(ServerParameters.SERVER_PORT);
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
