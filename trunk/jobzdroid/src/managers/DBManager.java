package managers;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import classes.DBconn;

/***
 * Manages DB queries
 */
public class DBManager {		
	
	public DBManager() {
	}
	
	private Connection getNewDBconn(){
		Connection c = null;
		try {
			c = new DBconn().getDBConnection();
		}
		catch (Exception e) {
			// TODO log error
			System.out.println("Error creating DB connection : " + e.getMessage());
		}
		return c;
	}
	
	/***
	 * Checks whether the given primary email address is unique
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean isUniqueEmailAddr(String email){
		Connection conn = getNewDBconn();	
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT AccountID FROM AccountTable " + "WHERE Email='" + email + "'"; 			
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			
			// check if ResultSet is empty  
			if (!rs.next())
				return true;
			else
				return false;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
		return false; 
	}
	
	/***
	 * Creates a new account with the given email, password, account type and person/company name
	 * @param email Primary email
	 * @param password User password
	 * @param accType Account type
	 * @param name Person/Company name
	 * @return boolean indicating whether account was successfully created
	 */
	public boolean createAccount(String email, String password, String accType, String name) {
		Connection conn = getNewDBconn();	
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
					
			String query = "INSERT INTO AccountTable(Email, Password, Type, Name, Status) VALUES " + 
	  		"('" + email + "','" + password + "','" + accType + "','" + name + "','" + "Pending" + "')";
			
			// if successful, 1 row should be inserted
			int rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted == 1)
				return true;
			else
				return false;					
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
		return false;		
	}

}
