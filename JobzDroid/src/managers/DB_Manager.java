package managers;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DB_Manager {
	
	protected ResultSet rs;
	protected Statement stm;
	
	private Connection db_conn = null;

	
	private Connection get_connection() {
		Connection m_conn = null;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			m_conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/craigsbay",
			        "root", "cs319CS#!(");
			
			System.out.println("Succesfully Connected");
		}
		catch(Exception e){
			//TODO: log error
			System.out.println("Error creating DB connection : " + e.getMessage());

		}
		
		return m_conn;
	}
	
	public DB_Manager()
	{
		db_conn = get_connection();
	}
	
	/***
	 * Checks whether the given primary email address is unique.
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean check_unique_mail(String email) {
		Connection conn = get_connection();	
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
	 * with a uniquely generated verification number used for email verification.
	 * New accounts open with "Pending" status.
	 * @param email Primary email
	 * @param password User password
	 * @param accType Account type
	 * @param name Person/Company name 
	 * @param uuid randomly generated unique verification number for email
	 * @return boolean indicating whether account was successfully created

	 */
	public boolean createAccount(String email, String password, String accType, String name, UUID uuid) {
		Connection conn = get_connection();	
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
					
			String query = "INSERT INTO AccountTable(Email, Password, Type, Name, Status, VerificationNum) VALUES " + 
	  		"('" + email + "','" + password + "','" + accType + "','" + name + "','" + "Pending" + "','" + uuid + "')";
			
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
	