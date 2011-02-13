package managers;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DB_Manager {
	
	protected ResultSet rs;
	protected Statement stm;
	
	private Connection db_conn=null;

	
	public Connection db_get_connection() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection m_conn = null;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			m_conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/craigsbay",
			        "root", "cpsc410");
			
			System.out.println("Succesfully Connected");
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
		return m_conn;
	}
	
	public DB_Manager()
	{
		
		try {
			db_conn = db_get_connection();
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
//	public int createNewAuction(String auctionName, String category, String auctionStatus, String creationDate, String expiryDate, long expiryTime, Double minPrice, int ownerID, String flickrAlbumID, String auctionDescription   )
//	{
//		try {
//			stm = db_conn.createStatement();
//			auctionName = auctionName.replace("\'", "\\\'");
//			auctionName = auctionName.replace("\"", "\\\"");
//			auctionName = auctionName.replace(";", "");
//			auctionName = auctionName.replace("{", "");
//			auctionName = auctionName.replace("}", "");
//			auctionName = auctionName.replace("<", "");
//			auctionName = auctionName.replace(">", "");
//			auctionName = auctionName.replace("^", "");
//			auctionDescription = auctionDescription.replace("\'", "\\\'");
//			auctionDescription = auctionDescription.replace("\"", "\\\"");
//			auctionDescription = auctionDescription.replace(";", "");
//			auctionDescription = auctionDescription.replace("{", "");
//			auctionDescription = auctionDescription.replace("}", "");
//			auctionDescription = auctionDescription.replace("<", "");
//			auctionDescription = auctionDescription.replace(">", "");
//			auctionDescription = auctionDescription.replace("^", "");
//			
//			String query = "INSERT INTO AuctionsTable(AuctionTitle, Category, AuctionStatus, " +
//								"ExpiryDate, AuctionExpireTime, CreationDate, OwnerID, MinPrice, LatestBidPrice, FlickerAlbumID, AuctionDescription) VALUES" +
//								"('" + auctionName + "' , '" + category + "' , '" + auctionStatus +
//								"' , '"  + expiryDate + "' , '" + expiryTime + "' , '" + creationDate + 
//								"' , '" + ownerID + "' , '" + minPrice + "' , '" + minPrice + "' , '" + flickrAlbumID + "' , '" + auctionDescription + "')"; 
//			
//			System.out.println("Creating new auction : " + query);
//			
//			stm.executeUpdate(query);
//			
//			query = "SELECT AuctionID FROM AuctionsTable WHERE "
//				+ " AuctionTitle='" + auctionName + "'" + " AND ExpiryDate='" + expiryDate + "'" + 
//						" AND AuctionExpireTime='" + expiryTime + "' AND ownerID='" + ownerID + "' AND creationDate='" + creationDate+"'"; 
//			ResultSet result = stm.executeQuery(query);
//			
//			
//			System.out.println("Debug Check " + query);
//			
//			if( result.first() )
//			{
//				int AuctionID = result.getInt("AuctionID");
//				System.out.println("Returned AuctionID: " + AuctionID);
//				stm.close();
//				return AuctionID;
//			}
//			else
//			{
//				System.out.println("Error: result.first() is false ");
//				//TODO: implement handling fail creation
//			}
//			stm.close();
//			
//			
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		
//		return -1;
//	}
	
	
//	public int auctionChangeStatus(String auctionID, String status){
//		
//		try{
//			stm = m_conn.createStatement();
//			
//			String query = "UPDATE " + "AuctionsTable" +
//							" SET auctionStatus='" + status +
//							"' WHERE auctionID=" + auctionID;
//			System.out.println(query);
//			
//			stm.execute(query);
//
//			return 1;
//			
//		
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		
//		
//		return 0;
//	}

	
//	public ArrayList<Auction> searchAuctionResults(String searchTitle, String searchCategory, String SearchOwner)
//	{
//		ArrayList<Auction> auctionList = new ArrayList<Auction>();
//		String ownerQuery;
//		
//		try {
//			stm = m_conn.createStatement();
//			
//			//Determine whether to search owner
//			if(SearchOwner == ""){
//				ownerQuery = "";
//			}
//			else
//			{
//				ownerQuery = "AND OwnerID = (SELECT UserID FROM UserTable WHERE UserName = '" + SearchOwner + "')";
//			}
//
//			String query = "SELECT * FROM AuctionsTable "
//							+ "WHERE AuctionTitle LIKE '" + searchTitle + "%' " //+
//							//" AND AuctionExpireTime>" + Calendar.getInstance().getTimeInMillis();
//							+ " AND Category LIKE '" + searchCategory + "' " + 
//							ownerQuery;
//			
//			System.out.println(query);
//			boolean success = stm.execute(query);
//			
//			ResultSet result = stm.getResultSet();
//			
//			while( result.next() ) {
//				Auction tempAuction = new Auction();
//				
//				tempAuction.auctionTitle = result.getString("AuctionTitle");
//				tempAuction.auctionID = result.getInt("AuctionID");
//
//				tempAuction.expiryDate = result.getString("ExpiryDate");
//				tempAuction.creationDate = result.getString("CreationDate");
//				tempAuction.category = result.getString("Category");
//				tempAuction.ownerID = result.getInt("OwnerID");
//				tempAuction.lastBidderID = result.getInt("lastBidderID");
//				tempAuction.minPrice = result.getDouble("MinPrice");
//				tempAuction.latestBidPrice = result.getDouble("LatestBidPrice");
//				tempAuction.bidCounter = result.getInt("BidCounter");
//				tempAuction.auctionStatus = result.getString("auctionStatus");
//				tempAuction.flickerAlbumID = result.getString("flickerAlbumID");
//				tempAuction.numberOfViews = result.getInt("numberOfViews");
//				
//				auctionList.add( tempAuction );
//				
//			}
//			
//
//			stm.close();
//			
//			System.out.println("Searched Auctions");
//			return auctionList;
//			
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		
//		
//		return null;
//			
//		}
	

//	
//	public boolean deleteFriend(int userID, int friendID ){
//		
//		try {
//			stm = m_conn.createStatement();
//			
//			String query = "DELETE FROM FriendsTable " +
//							"WHERE UserID= '" + userID + "' AND " +
//							"FriendID = '" + friendID + "'";
//			
//			boolean success = stm.execute(query);
//			stm.close();
//			
//			return success;
//			
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		return true;
//	}
//	
//	public int flagAuction(String auctionID){
//		try {
//			stm = m_conn.createStatement();
//			
//			String query = "UPDATE AuctionsTable" +
//							" SET Flag='1'" +
//							" WHERE AuctionID=" + auctionID;
//			
//			System.out.println("Flagging Auction: " + query);
//			
//			stm.executeUpdate(query);
//			stm.close();
//			
//			return 1;
//			
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		
//		
//		return 0;
//	}

	
	
}
