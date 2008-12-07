/**
 * 
 */
package edu.columbia.voip.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.columbia.voip.user.CalendarAccount;
import edu.columbia.voip.user.JabberAccount;
import edu.columbia.voip.user.GatewayUser;

public class DBEngine {

	private Connection _dbConn = null;
	
	// FIXME: not the right query
	private String QUERY_GET_ALL_USERS = "SELECT * FROM registered_users";
	
    private DBEngine() throws DatabaseException
	{
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
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Retreiving registered users from database.");
		List<GatewayUser> allUsers = new ArrayList<GatewayUser>();
		ResultSet resultSet = null;
        
		try {
	        Statement statement= _dbConn.createStatement();
			resultSet = statement.executeQuery(QUERY_GET_ALL_USERS);
		
			while (resultSet.next())
			{
				// FIXME: wrong database columns.. update when DB created.
				CalendarAccount calAccount = getCalendarAccount(resultSet);
				// FIXME: wrong database columns.. update when DB created.
				JabberAccount jabAccount = getJabberAccount(resultSet);
				allUsers.add(GatewayUser.createUser(calAccount, jabAccount));
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
				resultSet.getString("host"),
				resultSet.getInt("port"),
				resultSet.getBoolean("ssl")
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
					resultSet.getString("username"),
					resultSet.getString("password").toCharArray(),
					resultSet.getString("host"),
					resultSet.getString("uri"),
					resultSet.getInt("port"),
					resultSet.getBoolean("ssl")
					);
		return c;
	}
}

