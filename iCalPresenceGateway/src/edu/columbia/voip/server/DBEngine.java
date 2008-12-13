/**
 * 
 */
package edu.columbia.voip.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.server.conf.ServerParameters;
import edu.columbia.voip.user.CalendarAccount;
import edu.columbia.voip.user.JabberAccount;
import edu.columbia.voip.user.GatewayUser;

public class DBEngine {

	private Connection _dbConn = null;
	
	private final String QUERY_GET_ALL_USERS = "SELECT * FROM registrations";
	
	private Logger _logger = null;
	
    private DBEngine() throws DatabaseException
	{
    	this._logger = Logger.getLogger(getClass().getName());
		
    	// Register MySQL driver
    	try { Class.forName("com.mysql.jdbc.Driver").newInstance(); }
    	catch (InstantiationException e) { throw new DatabaseException(e); }
    	catch (IllegalAccessException e) { throw new DatabaseException(e); }
    	catch (ClassNotFoundException e) { throw new DatabaseException(e); }

    	String mysqlUrl = createMysqlURL();
    	try {_dbConn = DriverManager.getConnection(mysqlUrl, ServerParameters.MYSQL_USERNAME, 
    														 ServerParameters.MYSQL_PASSWORD); }
    	catch (SQLException e) { throw new DatabaseException(e); }
    }
    
    public static DBEngine buildConnection() throws DatabaseException
    {
    	DBEngine db = new DBEngine();
    	return db;
    }
    
    public void closeConnection() throws DatabaseException
    {
    	try { _dbConn.close(); }
    	catch (SQLException e) { throw new DatabaseException(e); }	
    }

    private String createMysqlURL()
	{
    	return 	"jdbc:mysql://" + ServerParameters.MYSQL_HOSTNAME + ":" + ServerParameters.MYSQL_PORT + 
				"/" + ServerParameters.MYSQL_REGISTRATION_DB;
	}

    /**
     * Uses already open connection to MySQL database and retrieves
     * list of registers users as a Java List structure.
     * @return List of registered users with all their Calendar and Jabber
     * account credentials
     * @throws DatabaseException
     */
	public List<GatewayUser> getAllRegisteredUsers() throws DatabaseException
    {
		_logger.log(Level.INFO, "Retreiving registered users from database.");
		List<GatewayUser> allUsers = new ArrayList<GatewayUser>();
		ResultSet resultSet = null;
        
		try {
	        Statement statement= _dbConn.createStatement();
			resultSet = statement.executeQuery(QUERY_GET_ALL_USERS);
		
			while (resultSet.next())
			{
				String primaryKey = resultSet.getString("cuid");
				CalendarAccount calAccount = getCalendarAccount(resultSet);

				// TODO: dropping Jabber support for now. 
				JabberAccount jabAccount = null;//getJabberAccount(resultSet);
				allUsers.add(GatewayUser.createUser(primaryKey, calAccount, jabAccount));
			}
		}
		catch (SQLException e) { throw new DatabaseException(e); }
		
        return allUsers;
    }

	/**
	 * Helper for creating Jabber Account from SQL resultset
	 * @param resultSet
	 * @return JabberAccount
	 * @throws SQLException
	 */
	private JabberAccount getJabberAccount(ResultSet resultSet) throws SQLException
	{
		JabberAccount j = new JabberAccount(
				resultSet.getString("username"),
				resultSet.getString("password").toCharArray(),
				resultSet.getString("host")
				);
		return j;
	}

	/**
	 * Helper for creating Calendar Account from SQL resultset
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private CalendarAccount getCalendarAccount(ResultSet resultSet) throws SQLException
	{
		CalendarAccount c = new CalendarAccount(
					resultSet.getString("ical_userid"),
					resultSet.getString("ical_passwd").toCharArray(),
					resultSet.getString("ical_host"),
					resultSet.getString("ical_uri"),
					resultSet.getInt("ical_port"),
					resultSet.getBoolean("ical_ssl")
					);
		return c;
	}
}

