/*
 * Logger.java
 *
 * Created on November 1, 2008, 7:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.columbia.voip.server;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

/**
 *
 * @author jmoral
 */
public class Logger {
    
    private static volatile PrintWriter _instance;
 
    // Protected constructor is sufficient to suppress unauthorized calls to the constructor
    protected Logger() {}

    public static PrintWriter getInstance() {
        if (_instance == null) {
            synchronized(Logger.class) {
                if (_instance == null) {
                    try { _instance = new PrintWriter(ServerParameters.LOGFILE); }
                    catch (FileNotFoundException e) { e.printStackTrace(); }
                }
            }
        }
        return _instance;
    }

}
