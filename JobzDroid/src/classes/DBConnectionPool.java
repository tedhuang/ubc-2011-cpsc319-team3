package classes;

import java.sql.*;
import java.util.List;

public class DBConnectionPool {
	private String URL, user, password;
	private int maxConnections, connectionsInUse;
	private List<Connection> freeConnections;
	
	/***
	 * DBConnectinPool constructor
	 * @param URL Database URL.
	 * @param user Database user name.
	 * @param password Database password.
	 * @param maxConnections Maximum amount of connections allowed. A value of 0 represents unlimited connections. 
	 */
	public DBConnectionPool(String URL, String user, String password, int maxConnections) {
        this.URL = URL;
        this.user = user;
        this.password = password;
        this.maxConnections = maxConnections;	// no limit on connections if value is 0
    }

	/***
	 * Returns a new database connection.
	 * @return A new database connection.
	 */
	private Connection createNewConnection() {
        Connection connection = null;
        try {
        	connection = DriverManager.getConnection(URL, user, password);
        }
        catch (SQLException e) {
            Utility.getErrorLogger().severe("Can't create a new connection for " + URL + ": " + e.getMessage());
            return null;
        }
        return connection;
    }

	/***
	 * Tries to get an active connection from the connection pool if connections in use has not reached the limit.
	 * If no active connections are available, then creates and returns a new connection.
	 * @return An active database connection.
	 */
    public synchronized Connection getConnection() {
        Connection connection = null;        
        // return null if the amount of connections has reached the limitation
        if (connectionsInUse >= maxConnections && maxConnections != 0)
        	return null;        
        // try to get an active connection from the pool
        while (freeConnections.size() > 0 && connection == null) {
            // Pick the first Connection in the list to get round-robin usage
            connection = (Connection) freeConnections.get(0);
            freeConnections.remove(0);
            // if bad connection, then set connection back to null and keep looping
            try {
				if (connection.isClosed())
					connection = null;
			}
            catch (SQLException e) {
            	connection = null;
			}
        }
        // if no restriction on amount of connections or no active connection was available in the pool,
        // then create new connection
        if (maxConnections == 0) {
        	connection = createNewConnection();
        }
        if (connection != null) {
        	connectionsInUse++;
        }
        return connection;
    }
    
}
