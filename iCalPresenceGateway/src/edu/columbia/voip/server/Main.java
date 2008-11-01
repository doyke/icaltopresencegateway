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
import java.util.Date;

/**
 *
 * @author jmoral
 */
public class Main {
    
    
    private static Thread _serverThread = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Logger.getInstance().println("Starting up Server: " + new Date());
        
        try {
            _serverThread = new Thread(new Server());
            _serverThread.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace(Logger.getInstance());
        }
        
    }
    
}
