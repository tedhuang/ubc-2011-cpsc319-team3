package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Utility;

import managers.DBManager;
import managers.EmailManager;

public class ServletProfile extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;

	public ServletProfile() {
        super();
		dbManager = DBManager.getInstance();
    }
	
	private enum EnumAction
	{ 
		getProfile,
		//updatePublicProfile,
		updateAccounttSetting,
		createProfile,
		editProfile,
		UNKNOWN;
	private static EnumAction getAct(String Str)//why static?
	{
	try {return valueOf(Str);}
	catch (Exception ex){return UNKNOWN;}
	}
	};
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = request.getParameter("action");
		switch(EnumAction.getAct(action)){
		
			case getProfile:
			
				break;
			case updateAccounttSetting:
				
				break;
			case createProfile:
				createProfile(request, response); //implement error checks
				break;
				
			case editProfile:
			
				break;
			default:
				System.out.print("Dont you try to hack =D");
				break;
		
		}//ENDOF SWITCH
	
	}//ENDOF processReq Func
	
	
	private void getProfile(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		int accountID = Integer.parseInt(request.getParameter("accountID"));
		int accountType;
		String name;
		String secEmail;
		String phone;
		String selfDescription;
		String empPref;
		int educationLevel;
		
		
	}
	
	
	private void createProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		System.out.println("Checkpoint: Inside createProfile");
		
		//Poster = 1, Searcher = 2
		int accountType = Integer.parseInt(request.getParameter("accountType")); 	
		int accountID = Integer.parseInt(request.getParameter("accountID"));
		
		String name;
		String secEmail;
		String phone;
		String selfDescription;
		String empPref;
		int educationLevel;
		
		//Initialize Return statments
		boolean isSuccessful = false;
		String message = "Failure to create new profile";
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try{
		//Create Job Poster Account
			if(accountType == 1){
				System.out.println("Creating Job Poster Account");
				stmt = conn.createStatement();
				
				name = request.getParameter("posterName");
				secEmail = request.getParameter("posterSecEmail");
				phone = request.getParameter("posterPhone");
				selfDescription = request.getParameter("posterDescription");

				//Check format
				name = Utility.checkInputFormat( name );
				secEmail = Utility.checkInputFormat( secEmail );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );
				
				String query = 
					"UPDATE tableProfilePoster SET "+ 
							"name = " + name + "," +
							"phone = " + phone + "," +
							"selfDescription = " + selfDescription + "," +
					"WHERE idAccount=" + accountID;
					
				
				System.out.println("New PosterProfile Query:" + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted == 1){
					System.out.println("New Profile Creation success (DB)");
				}
				else{
					System.out.println("Error: row not inserted");
					stmt.close();
				}
				
				//Check if profile is created successfully
				query = "SELECT idAccount FROM tableProfilePoster WHERE " + " idAccount='" + accountID + "'"; 
				ResultSet result = stmt.executeQuery(query);
				if( result.first() )
				{
					System.out.println("Profile Created in DB");
					isSuccessful = true;
					message = "Create new profile success";
				}
				else
				{
					System.out.println("Error: result.first() is false ");
				}
				
			}
			
		//Create Job Searcher Account
			else{
				System.out.println("Creating Job Searcher Account");
				
				name = request.getParameter("searcherName");
				secEmail = request.getParameter("searcherSecEmail");
				phone = request.getParameter("searcherPhone");
				selfDescription = request.getParameter("searcherDescripton");
				empPref = request.getParameter("empPref");
				educationLevel = Integer.parseInt(request.getParameter("educationLevel"));
			
				stmt = conn.createStatement();
				
				//Check format
				name = Utility.checkInputFormat( name );
				secEmail = Utility.checkInputFormat( secEmail );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );

				//TODO: include address
				String query = 
					"UPDATE tableProfileSearcher SET "+ 
						"name = " + name + "," +
						"phone = " + phone + "," +
						"selfDescription = " + selfDescription + "," +
						"educationLevel = " + educationLevel + "," +
					"WHERE idAccount=" + accountID;

				// if successful, 1 row should be inserted
				System.out.println("New SearcherProfile Query:" + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted == 1){
					System.out.println("New Profile Creation success (DB)");
				}
				else{
					stmt.close();
				}
				
				//Check if profile is created successfully
				query = "SELECT name FROM tableProfileSearcher WHERE " + " idAccount='" + accountID + "'"; 
				ResultSet result = stmt.executeQuery(query);
				if( result.first() )
				{
					System.out.println("Profile Created in DB");
					isSuccessful =  true;
					message = "Create new profile success";
				}
				else
				{
					System.out.println("Error: result.first() is false ");
				}
				
				
			}
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
	    
	    System.out.println("Checkpoint: End of create profile - Message: " + message);
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	    

	    
	}
	
	
	private void editProfile(HttpServletRequest request, HttpServletResponse response) 
								throws ServletException, IOException{
		
		//Poster = 1, Searcher = 2
		int accountType = Integer.parseInt(request.getParameter("strAccountType")); 	
		int accountID = Integer.parseInt(request.getParameter("accountID"));
		
		String name;
		String secEmail;
		String phone;
		String selfDescription;
		String empPref;
		int educationLevel;
		
		//Initialize return statements
		boolean isSuccessful = false;
		String message = "failed to edit profile";
		
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try{
		//Edit Job Poster Account
			if(accountType == 1){

				stmt = conn.createStatement();
				
				name = request.getParameter("posterName");
				secEmail = request.getParameter("posterSecEmail");
				phone = request.getParameter("posterPhone");
				selfDescription = request.getParameter("posterDescription");
				
				name = Utility.checkInputFormat( name );
				secEmail = Utility.checkInputFormat( secEmail );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );
				
				String query = 
					"UPDATE tableProfilePoster SET " 
					+ "name='" 				+ name + "','" 
					+ "secondaryEmail='" 	+ secEmail + "','" 
					+ "phone='" 		+ phone + "','" 
					+ "selfDescription='" 	+ selfDescription + 
					"WHERE idAccount='" 	+ accountID + "' ";
					
				
				System.out.println("New PosterProfile Query:" + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted == 1){
					System.out.println("Edit Profile success");
				}
				else{
					System.out.println("Error: row not inserted");
					stmt.close();
				}
				
				//Check if profile is created successfully
				query = "SELECT accountID FROM tableProfilePoster WHERE " + " accountID='" + accountID + "'"; 
				ResultSet result = stmt.executeQuery(query);
				if( result.first() )
				{
					int idCheck = result.getInt("accountID");
					System.out.println("Profile Created in DB with accountID: " + idCheck);
					isSuccessful = true;
					message = "Edit profile success";
				}
				else
				{
					System.out.println("Error: result.first() is false ");
				}
				
			}
			
		//Edit Job Searcher Account
			else{
				name = request.getParameter("searcherName");
				secEmail = request.getParameter("searcherSecEmail");
				phone = request.getParameter("searcherPhone");
				selfDescription = request.getParameter("searcherDescripton");
				empPref = request.getParameter("empPref");
				educationLevel = Integer.parseInt(request.getParameter("educationLevel"));
			
				stmt = conn.createStatement();
				
				name = Utility.checkInputFormat( name );
				secEmail = Utility.checkInputFormat( secEmail );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );

				//TODO: include address
				String query = 
					"UPDATE tableProfileSearcher SET " 
					+ "name= '" 			+ name + "','" 
					+ "secondaryEmail='" 	+ secEmail + "','" 
					+ "phone='" 		+ phone + "','" 
					+ "selfDescription='" 	+ selfDescription + "','"
					+ "empPref='" 			+ empPref + "','"
					+ "educationLevel='" 	+ educationLevel + "' " +
					"WHERE idAccount='" 	+ accountID + "' ";
					
				
				// if successful, 1 row should be inserted
				System.out.println("New SearcherProfile Query:" + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted == 1){
					System.out.println("New Profile Creation success (DB)");
				}
				else{
					stmt.close();
				}
				
				//Check if profile is created successfully
				query = "SELECT accountID FROM tableProfileSearcher WHERE " + " accountID='" + accountID + "'"; 
				ResultSet result = stmt.executeQuery(query);
				if( result.first() )
				{
					int idCheck = result.getInt("accountID");
					System.out.println("Profile Created in DB with accountID: " + idCheck);
					isSuccessful =  true;
				}
				else
				{
					System.out.println("Error: result.first() is false ");
				}
				
				
			}
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
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}

}






