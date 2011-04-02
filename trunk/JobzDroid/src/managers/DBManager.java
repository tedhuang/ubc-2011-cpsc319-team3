package managers;

import java.sql.*;
import classes.DBConnectionPool;
import classes.Utility;

/***
 * Manages the DB connection pool, and provides frequently used database functions.
 */
public class DBManager {			
	private DBConnectionPool connectionPool;
	// singleton class constructor
	private static DBManager dbManagerInstance = null;
	
	protected DBManager() {
		// register jdbc driver
		try{
			Driver driver = (Driver) Class.forName(SystemManager.dbDriver).newInstance();
			DriverManager.registerDriver(driver);
		}
		catch(Exception e){
			Utility.logError("Failed to register JDBC driver: " + e.getMessage());
		}
		// create connection pool
		connectionPool = new DBConnectionPool
			(SystemManager.dbURL, SystemManager.dbUser, SystemManager.dbPassword, SystemManager.maxDBConnectionPoolSize);
	}
	
	public static DBManager getInstance() {
		if(dbManagerInstance == null) {
			dbManagerInstance = new DBManager();
		}
		return dbManagerInstance;
	}
	
	/***
	 * Gets an active connection from the connection pool.
	 * @return An active connection.
	 */
	public Connection getConnection(){
		return connectionPool.getConnection();
	}
	
	/***
	 * Frees and returns a connection to the connection pool.
	 */
	public void freeConnection(Connection connection){
		connectionPool.returnConnectionToPool(connection);
	}	
}