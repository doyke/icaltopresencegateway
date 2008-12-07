/**
 * 
 */
package edu.columbia.voip.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.user.GatewayUser;

/**
 * Main server thread where all the work happens for the gateway.
 * Thread is responsible for polling each of the registered user's
 * iCalendar accounts and passing their calendaring info to the presence
 * server. 
 * @author jmoral
 */
public class GatewayThread implements Runnable
{
	
	private static List<GatewayUser> _gatewayUsers = null; 

	public GatewayThread(List<GatewayUser> list)
	{
		this._gatewayUsers = list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		/**
		 * GAMEPLAN:
		 * 
		 * 1.) SPAWN <THREAD_POOL_SIZE> NUMBER OF THREADS FOR SYNCING USER'S CALENDARS TO PRESENCE SERVER
		 * 2.) SLEEP FOR 
		 */
		try { Thread.sleep(ServerParameters.POLL_INTERVAL); }
		catch (InterruptedException e) { Logger.getLogger(getClass().getName()).log(Level.INFO, "Interrupted exception caught", e); }
	}

}
