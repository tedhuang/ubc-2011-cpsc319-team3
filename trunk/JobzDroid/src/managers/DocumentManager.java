package managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import classes.Document;
import classes.Utility;

/***
 * Provides functions to execute automated tasks and loading system variables from configuration file.
 * Singleton class.
 */
public class DocumentManager {	
	// singleton instance
	private DBManager dbManager;
	private static DocumentManager docManagerInstance = null;
	protected DocumentManager() {
		dbManager = DBManager.getInstance();
	}	
	public static DocumentManager getInstance() {
		if(docManagerInstance == null)
			docManagerInstance = new DocumentManager();
		return docManagerInstance;
	}
		
	/***
	 * Looks for the document with the provided Document ID.
	 * @param idDoc Document ID to look for.
	 * @return Document object with the given Document ID.
	 */
	public Document getDocumentById(int idDoc){
		Document doc = null;
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		int idAccount;
		String docPath, docName;
		long dateTimeUploaded;
		
		try {
			stmt = conn.createStatement();
			String query = "SELECT * FROM tableDocument WHERE idDoc='" + idDoc + "';";            
			rs = stmt.executeQuery(query);
			if(rs.first()){
				idAccount = rs.getInt("idAccount");
				docPath = rs.getString("docPath");
				docName = rs.getString("docName");
				dateTimeUploaded = rs.getLong("dateTimeUploaded");
				doc = new Document(idDoc, idAccount, docPath, docName, dateTimeUploaded);
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
	    return doc;
	}
}
