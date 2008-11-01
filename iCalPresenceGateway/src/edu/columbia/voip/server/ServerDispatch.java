/*
 * ServerDispatch.java
 *
 * Created on November 1, 2008, 7:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

import java.net.Socket;

/**
 *
 * @author jmoral
 */
public class ServerDispatch extends Thread {
    
    private Socket _clientSocket = null;
    
    /** Creates a new instance of ServerDispatch */
    public ServerDispatch(Socket socket) {
        _clientSocket = socket;
    }
    
    public void run()
    {
        // TODO: handle client connection here
        Logger.getInstance().println("Got connection from: " + _clientSocket.getInetAddress().getHostAddress());
        Logger.getInstance().println("Begin reading/processing asynchronous message from iCal calendar server?");
    }
    
}
