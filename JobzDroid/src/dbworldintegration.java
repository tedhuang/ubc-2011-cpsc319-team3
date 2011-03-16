import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.jsoup.Jsoup;

import classes.Utility;

import managers.DBManager;

import sun.net.www.URLConnection;


/**
public class dbworldintegration {
    public static void main(String[] args) throws Exception {


	String inputLine;
	inputLine= Jsoup.connect("http://www.cs.wisc.edu/dbworld/browse.html").get().html();
	Parser parser = new Parser();
	parser.setInputHTML(inputLine);
	parser.setEncoding("UTF-8");
	NodeList n1 = parser.parse(null);
	NodeList trs = n1.extractAllNodesThatMatch(new TagNameFilter("tr"),true);
	
	for(int i=1;i<=10;i++) {
	    NodeList nodes = trs.elementAt(i).getChildren();
	    NodeList tds  = nodes.extractAllNodesThatMatch(new TagNameFilter("td"),true);

	    NodeList annNode = tds.elementAt(1).getChildren();
	    //Do stuff with jobAnnNode
	    String annNodeToString = annNode.asString();
	    if(annNodeToString.contains("conf. ann."))
	    {
	    	System.out.println(tds.asString());
	    	System.out.println(tds.elementAt(3).getChildren().asString());
	    }
	    
	} 
    }}
**/

//ToDo:
//(1.)	Program runs every time server is running. <--Not done
//(2a.)	Method 1: MessageCountListener-when inbox gets new email invoke method to read new email OR <-- Omit
//(2b.)	OR Method 2: Check inbox every time period  <--Not Done
//(3.)	Check message subject heading, find this heading in the website and see if this dbworld news is a "job ann." news. <--Done
//(4.)	If so, update to database. <-- Done
//(5.)	Delete the email <-- Done
//(6.)	Check next email  <-- Done

//(7.) Test if every sequence works <-- Not Done

public class dbworldintegration {
	private DBManager dbManager;
	
	public dbworldintegration(){
	dbManager= DBManager.getInstance();		
	}

//Invoke test program every time java application is started by user.	
	public static void main(String[] args)
	{
		String user="jobzdroidtestemail@gmail.com";
		String password="cpsc319team3";
		new dbworldintegration().emailParse(user,password);
	}

/*
 * emailParse(String username, String password)
 * 
 * The emailParse() method connects to the email account, reads through all email messages that are from
 * DBWorld and are job announcements in the DBWorld mailing list (aka "job.ann"). It then takes
 * the information (author, job description, job title, etc.) from these messages and creates 
 * a new JobzDroid Job Advertisement with it.
 * 
 * @param: username - The email account to connect to 
 * @param: password - The password of the email account
 * 
 */
	public void emailParse(String username,String password) {
		
		//Setup to connect to JobzDroid database. Create connection.
		Connection conn = dbManager.getConnection();
		Statement stmt = null;		
		
		//POP3 protocol to connect to email. The POP3 protocol works the following way:
		//POP3 opens the email account and downloads all the email messages from the email inbox.
		//It saves these email messages into its own POP3 INBOX folder. Hence, it must be noted
		//the email INBOX and the POP3 INBOX are two different things.
		String host = "pop.gmail.com";
		try {
			Properties prop = new Properties();
			prop.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			prop.setProperty("mail.pop3.socketFactory.fallback", "false");
			prop.setProperty("mail.pop3.port", "995");
			prop.setProperty("mail.pop3.socketFactory.port", "995");
       
			prop.put("mail.pop3.host", host);
			prop.put("mail.store.protocol", "pop3");
			Session session = Session.getDefaultInstance(prop);
			Store store = session.getStore();
			System.out.println("your ID is : "+ username);
			System.out.println("Connecting...");
			store.connect(host, username, password);
			System.out.println("Connected...");
			Folder inbox = store.getDefaultFolder().getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			Message[] msg = inbox.getMessages();
			
			//Create statement
			stmt = conn.createStatement();
        
			//Setup to connect to the DBWorld mailing list website, and to parse all relevant data
			//from the HTML table that lists DBWorld postings (job ann., conf ann., journal ann. etc.)
			String inputLine;
			inputLine= Jsoup.connect("http://www.cs.wisc.edu/dbworld/browse.html").get().html();
			Parser parser = new Parser();
			parser.setInputHTML(inputLine);
			parser.setEncoding("UTF-8");
			NodeList n1 = parser.parse(null);
			NodeList trs = n1.extractAllNodesThatMatch(new TagNameFilter("tr"),true);
        
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			for (int i = msg.length-1; i >= 0; i--) 
			{
				String getReplyTo = msg[i].getReplyTo()[0].toString();	
				String getSubject = msg[i].getSubject();
				getSubject = getSubject+" ";         
				//invoke searchDBWorldAnnPostings() method, and return boolean to var isJobAnn;
				boolean isJobAnn = searchDBWorldAnnPosting(trs, getSubject);
 
				// if email is from DBWorld mailing list, and isJobAnn:email posting is a job announcement, 
				// then take information from email and create new JobzDroid Job Ad. with it.
				if(getReplyTo.contentEquals("dbworld_owner@yahoo.com") && isJobAnn)
				{	  
					String from = msg[i].getFrom()[0].toString();
					String replyTo = msg[i].getReplyTo()[0].toString();
					String subject = msg[i].getSubject().toString();
					String allHeaders = msg[i].getAllHeaders().toString();
					Date sentDate = msg[i].getSentDate();					
					Calendar cal = Calendar.getInstance();
					cal.setTime(sentDate);
					long millisSentDate = -1;
					millisSentDate = cal.getTimeInMillis();
					String message = msg [i].getInputStream().toString();
					/**
					InputStream stream = msg[i].getInputStream();
					while (stream.available() != 0) {
						message = stream.toString();
					}**/
					
					createJobAdvertisementWithEmail(stmt, subject, message, millisSentDate/**,expiryDate**/);
					
					//Test code-print message header and body if success
					System.out.println("From : " + from);
					System.out.println("Reply to : "+ replyTo);
					System.out.println("Subject : " + subject);
					System.out.println("Date : " + sentDate);
					System.out.println("All Headers : " + allHeaders);        	  
					System.out.print(message);

					System.out.println();
					System.out.println("-----------------------------------------------------");
				}
         
				//Delete the read email message after. 
				//Test code-prompt user if they want to delete.
				System.out.println("Do you want to delete the message? -- " + msg[i].getSubject());
				String line = reader.readLine();
				if("Y".equalsIgnoreCase(line)){
					msg[i].setFlag(Flags.Flag.DELETED, true);
					System.out.println("Msg Deleted\n");
				}
				else
					System.out.println("Msg kept-proceeding to next msg\n");
         
			}/**end of forloop**/
			
			//Close connection
			inbox.close(true);
			store.close();       
    
		}/**end of try block**/
    
		catch (Exception e) {
			e.printStackTrace();
		} 
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
		
	}/**end of method receive(String username, String password)**/
	
	
/*
 * createJobAdvertisementWithEmail(Statement stmt, String subject, String message, long millisSentDate)
 * 
 * Sends information obtained from email message to JobAdTable in database as new entry.
 * 
 * @param: stmt - Database statement that executes/updates queries to the Database
 * @param: subject - Subject heading from email message
 * @param: message - Email message
 * @param: millisSentDate - Date the email was sent in milliseconds. Also used to compute JobAdID	
 * 
 */
	
	public void createJobAdvertisementWithEmail(Statement stmt, String subject, String message, long millisSentDate /**,expiryDate**/){
		try{
			int jobAdId = -1;
			String jobAdvertisementTitle = Utility.checkInputFormat(subject);
			String jobDescription = Utility.checkInputFormat(message);
		
			//Add new entry with specified parameters into database
			String query = 
				"INSERT INTO tableJobAd(title, description, datePosted) " +
				"VALUES " + "('" 
				+ jobAdvertisementTitle  + "','" 
				+ jobDescription  + "','" 
				+ millisSentDate	  +
				"')"; 
		
			//If successful, 1 row should be inserted
			System.out.println("New Job Ad query: " + query);
			int rowsInserted = stmt.executeUpdate(query);
			
			if(rowsInserted!= 1){
				System.out.println("New JobAd Creation failed");
				Utility.logError("New JobAd insert in DB failed");
			}

		//Get jobAdId
			query = "SELECT idJobAd FROM tableJobAd WHERE " +
				"title='" + jobAdvertisementTitle + "' AND " +
				"datePosted='" + millisSentDate + "'";
			ResultSet result = stmt.executeQuery(query);
		
			if(result.first()){
				jobAdId = result.getInt("idJobAd");
				System.out.println("Job Ad ID: " + jobAdId);
			}
			else{
				System.out.println("Error: Job Ad ID not found after creation");
			}

		}/**end of try block**/
		
		catch (SQLException e) {
			e.printStackTrace();
		} 
	}/**end of createJobAdvertismentWithEmail**/

/*
 * searchDBWorldAnnPosting(NodeList trs, String msgSubject)
 * 
 * This method sees if given email message is a job announcement posting (job ann.) 
 * in DbWorld mailing list website. 
 * 
 * @param: NodeList trs - a list of nodes that contain html table rows
 * @param: String msgSubject - email message Subject Heading
 *
 */
	public boolean searchDBWorldAnnPosting(NodeList trs, String msgSubject){
		boolean flag=false;
		try {
			for(int i=1;i<=10;i++) {
				//Filter through all <trs> and <tds> tags in html table
				NodeList nodes = trs.elementAt(i).getChildren();
				NodeList tds  = nodes.extractAllNodesThatMatch(new TagNameFilter("td"),true);
				
				//Get 2nd column in mailing list HTML table-Job Type
				NodeList annNode = tds.elementAt(1).getChildren();
				String annNodeToString = annNode.asString();
				
				//Get 4th column in mailing list HTML table-Posting Subject
				NodeList subjectNode = tds.elementAt(3).getChildren();
				String subjectNodeToString = subjectNode.asString();
				
				if(msgSubject.contains(subjectNodeToString) && annNodeToString.contains("job ann.")) 
				{
					flag=true;
					break; //break out of function
				}
				else
					flag=false;	
			}/**end of for loop**/ 
		}/**end of try block**/
	
		catch (Exception e) {
			e.printStackTrace();
		}    	
		return flag;
	}/**end of searchDBWorld()**/
		
}/**end of class dbworlintegration**/




