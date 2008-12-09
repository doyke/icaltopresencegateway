/**
 * 
 */
package edu.columbia.voip.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.server.conf.ServerParameters;
import edu.columbia.voip.user.GatewayUser;

/**
 * Main server thread where all the work happens for the gateway.
 * Thread is responsible for polling each of the registered user's
 * iCalendar accounts and passing their calendaring info to the presence
 * server. 
 * @author jmoral
 */
public class GatewayThread extends Thread
{
	
	private List<GatewayUser> _gatewayUsers = null;
	
	private ExecutorService _execService = null;

	public GatewayThread(List<GatewayUser> list)
	{
		this._gatewayUsers = list;
		_execService = Executors.newFixedThreadPool(ServerParameters.THREAD_POOL_SIZE);
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
		 * 2.) SLEEP FOR POLL_INTERVAL.
		 * 3.) AT ANY TIME, REGISTRATION THREAD CAN ADD REGISTERED USERS TO MY LIST.
		 */
		
		for (Iterator<GatewayUser> iter = _gatewayUsers.iterator(); iter.hasNext(); )
			_execService.execute(new GatewayDispatch(iter.next()));
		
		try { Thread.sleep(ServerParameters.POLL_INTERVAL); }
		catch (InterruptedException e) { Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Interrupted exception caught", e); }
	}

	public synchronized void addGatewayUser(Collection<GatewayUser> users) 	{ _gatewayUsers.addAll(users); }
	public synchronized void addGatewayUser(GatewayUser user) 				{ _gatewayUsers.add(user); }
	public List<GatewayUser> getGatewayUsers() 								{ return _gatewayUsers; }
}