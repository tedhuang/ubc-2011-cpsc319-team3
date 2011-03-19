package managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import classes.Utility;
import classes.NewsEntry;

/***
 * Provides functions to execute automated tasks and loading system variables from configuration file.
 * Singleton class.
 */
public class NewsManager {	
	// singleton instance
	private DBManager dbManager;
	private static NewsManager newsManagerInstance = null;
	protected NewsManager() {
		dbManager = DBManager.getInstance();
	}	
	public static NewsManager getInstance() {
		if(newsManagerInstance == null)
			newsManagerInstance = new NewsManager();
		return newsManagerInstance;
	}
		
	/***
	 * Adds a news entry to tableNews in the database
	 * @param title Title of the news.
	 * @param content Content of the news.
	 * @param dateTimePublished News publish date/time. Measured in milliseconds.
	 * @return boolean indicating whether the addition was successful.
	 */
	public boolean addNewsEntry(String title, String content, long dateTimePublished){
		Connection conn = dbManager.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String query = "INSERT INTO tableNews(title, content, dateTimePublished)" +
            		" VALUES(?,?,?);";
			pst = conn.prepareStatement(query);
			pst.setString(1, title);
            pst.setString(2, content);
            pst.setLong(3, dateTimePublished);
            
			int rowsInserted = pst.executeUpdate();
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
			return false;	
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (pst != null)
	                pst.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	}
	
	/***
	 * Deletes a news entry to tableNews in the database
	 * @param idNews ID of the news.
	 * @return boolean indicating whether the deletion was successful.
	 */
	public boolean deleteNewsEntry(int idNews){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String query = "DELETE FROM tableNews WHERE idNews='" + idNews + "';"; 	
            
			int rowsDeleted = stmt.executeUpdate(query);
			// if successful, 1 row should be deleted
			if (rowsDeleted != 1)
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
			return false;	
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	}
	
	/***
	 * Search for a NewsEntry in the database with the given ID.
	 * @param idNews ID of the news to search for.
	 * @return NewsEntry with the given ID. Null if not found.
	 */
	public NewsEntry getNewsEntryById(int idNews){
		NewsEntry entry = null;
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String title, content;
		long dateTimePublished;
		
		try {			
			stmt = conn.createStatement();
			String query = "SELECT * FROM tableNews WHERE idNews='" + idNews +" ';";            
			rs = stmt.executeQuery(query);
		    if(rs.first()) {
		    	title = rs.getString("title");
		    	content = rs.getString("content");
		    	dateTimePublished = rs.getLong("dateTimePublished");
		        entry = new NewsEntry(idNews, title, content, dateTimePublished);
		    }
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return entry;
	}
	
	/***
	 * Gets, sorts and returns all news entries from the database
	 * @return ArrayList of sorted news entries. (latest entries at the front) 
	 */
	public ArrayList<NewsEntry> loadNewsEntries(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;		
		ArrayList<NewsEntry> entries = new ArrayList<NewsEntry>();
		int idNews;
		String title, content;
		long dateTimePublished;
		
		try {			
			stmt = conn.createStatement();
			String query = "SELECT * FROM tableNews;";            
			rs = stmt.executeQuery(query);
		    while (rs.next()) {
		    	idNews = rs.getInt("idNews");
		    	title = rs.getString("title");
		    	content = rs.getString("content");
		    	dateTimePublished = rs.getLong("dateTimePublished");
		        NewsEntry entry = new NewsEntry(idNews, title, content, dateTimePublished);
		        entries.add(entry);
		    }
		    Collections.sort(entries);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
        return entries;
	}
}
