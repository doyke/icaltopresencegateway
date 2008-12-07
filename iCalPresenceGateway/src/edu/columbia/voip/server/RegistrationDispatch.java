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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmoral
 */
public class RegistrationDispatch extends Thread {
    
    private Socket _clientSocket = null;
    
    /** Creates a new instance of ServerDispatch */
    public RegistrationDispatch(Socket socket) {
        _clientSocket = socket;
    }
    
    public void run()
    {
        // TODO: handle client connection here
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Got connection from: " + _clientSocket.getInetAddress().getHostAddress());
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Begin reading/processing asynchronous message from iCal calendar server?");
    }
    
}
