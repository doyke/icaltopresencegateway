/*
 * ServerParameters.java
 *
 * Created on November 1, 2008, 7:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

/**
 *
 * @author jmoral
 */
public class ServerParameters {
    
    // Filename of server log that record debug messages/events.
    public static final String LOGFILE      = "server.log";
    
    // I guess this will be changed later to the port on which iCal messages are sent?
    public static final int SERVER_PORT     = 1783;
    
    /** parameters cannot be instantiated-- just accessed globally and readonly */
    private ServerParameters() {
    }
    
}
