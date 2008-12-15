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
	
	private Logger _logger = null;

	public GatewayThread(List<GatewayUser> list)
	{
		this._gatewayUsers = list;
		this._logger = Logger.getLogger(getClass().getName());
		
		if (ServerParameters.doThreadPooling())
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
		
		while (true)
		{
			for (Iterator<GatewayUser> iter = _gatewayUsers.iterator(); iter.hasNext(); )
			{
				GatewayUser user = iter.next();
				if (ServerParameters.doThreadPooling())
				{
					//_logger.log(Level.INFO, "Launching another thread from pool for user: " + user.getPrimaryKey());
					_execService.execute(new GatewayDispatch(user));
				}
				else
				{
					//_logger.log(Level.INFO, "Launching thread for user: " + user.getPrimaryKey());
					Thread dispatch = new Thread(new GatewayDispatch(user));
					dispatch.start();
				}
			}
			
			try { Thread.sleep(ServerParameters.POLL_INTERVAL); }
			catch (InterruptedException e) { _logger.log(Level.SEVERE, "Interrupted exception caught", e); }
			
			_logger.log(Level.INFO, "Finished sleeping for " + (ServerParameters.POLL_INTERVAL / 1000 ) + 
									" seconds, about to do executeService again.");
		}
	}

	public synchronized void addGatewayUser(Collection<GatewayUser> users) 	{ _gatewayUsers.addAll(users); }
	public synchronized void addGatewayUser(GatewayUser user) 				{ _gatewayUsers.add(user); }
	public List<GatewayUser> getGatewayUsers() 								{ return _gatewayUsers; }
}