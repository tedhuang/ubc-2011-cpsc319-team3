package managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.io.IOUtils;

import classes.Utility;

import managers.DBManager;

import sun.net.www.URLConnection;


//ToDo:
//(1.)	Program runs every time server is running. <--Not done
//(2.)	Check inbox every time period (1hr?) <--Not Done
//(3a.)	Check message subject heading-is this from DBWorld? <-- Done
//(3b.) Check thru headers-is this of Job Annoucement? <-- Done
//(4.)	If so, create Job Ad with message and update to database. <-- Done
//(5.)	Delete the email <-- Done
//(6.)	Check next email  <-- Done

//(7.) Test if every sequence works <-- Not Done-need to test (1.) and (2.)

public class dbworldintegration {
	private DBManager dbManager;	
	private String user;
	private String password;
	
/*
 * dbworldintegration()
 * 
 * Constructor for dbworldintegration class
 * 
 */
	public dbworldintegration(){
		dbManager= DBManager.getInstance();
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
			
			//Create statement to pass sql queries.
			stmt = conn.createStatement();

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Total number of messages in INBOX: "+msg.length);
			for (int i = msg.length-1; i >= 0; i--) 
			{
				String getReplyTo = msg[i].getReplyTo()[0].toString();	
				String getSubject = msg[i].getSubject();
				getSubject = getSubject+" ";         
				
	            Enumeration allHeaders = msg[i].getAllHeaders();
	            int num = 0;
	            while (allHeaders.hasMoreElements()) {
	                Header h = (Header) allHeaders.nextElement();
	                if(h.getValue().contains("conference/announcement"))
	                	System.out.println("conference!");
	                System.out.println(num+ h.getName() + ": " + h.getValue());
	                num++;
	            }
				// if email is from DBWorld mailing list, and isJobAnn:email posting is a job announcement, 
				// then take information from email and create new JobzDroid Job Ad. with it.
				
				// testCode: set conditional statement to always true so that all email are read (including
				// test messages)
				if(getReplyTo.contentEquals("dbworld_owner@yahoo.com"))
				{	
					boolean isJobAnn= false;
					
			        Enumeration annHeaders = msg[i].getAllHeaders();
					isJobAnn = checkHeaderForJobAnn(annHeaders);
					
					if(isJobAnn)
					{						
						String from = msg[i].getFrom()[0].toString(); //from header
						String replyTo = msg[i].getReplyTo()[0].toString(); //reply-to header
						String subject = msg[i].getSubject().toString(); //subject header
						
						Date sentDate = msg[i].getSentDate(); //date-sent header				
						Calendar cal = Calendar.getInstance();
						cal.setTime(sentDate);
						long millisSentDate = -1; //date-sent header set to milliseconds
						millisSentDate = cal.getTimeInMillis();
						
						Enumeration sdHeaders = msg[i].getAllHeaders(); //start-date header
						Date startDate = getStartDate(sdHeaders); 
						long millisStartDate = Utility.getCurrentTime();
						millisStartDate = cal.getTimeInMillis();
												
						if(startDate!=null){
							cal.setTime(startDate);
							millisStartDate = cal.getTimeInMillis();							
						}
						
						Enumeration edHeaders = msg[i].getAllHeaders(); //expiry-date header
						Date expiryDate = getExpiryDate(edHeaders); 
						long millisExpiryDate = 30*24*60*60*1000;
						
						if(expiryDate !=null){
							cal.setTime(expiryDate);
							millisExpiryDate = cal.getTimeInMillis();
						}
						
						/**String location = getLocation(headers); //location header**/
						
						Enumeration wpHeaders = msg[i].getAllHeaders(); //web-page header (to store as contact info.)
						String webPage = getWebPage(wpHeaders); 
												
						String message = null; //message body
						InputStream stream = msg[i].getInputStream(); 
						while(stream.available()!= 0) {
							StringWriter writer = new StringWriter();
							IOUtils.copy(stream, writer);
							message = writer.toString();
						}
						
						//Test code-print message headers and body if success
						System.out.println("From:" + from);
						System.out.println("Reply to:"+ replyTo);
						System.out.println("Subject:" + subject);
						System.out.println("Date:" + sentDate);
						System.out.println("Date in millisec:" + millisSentDate);

						System.out.print(message);
						System.out.println();
						System.out.println("-----------------------------------------------------");
						
						/**
						//Test Code: ask user if they want to create Job Ad with current email message.
						System.out.println("Do you want to create a Job Ad. with this message? Enter 'Y' - " + subject);
						String prompt = reader.readLine();
						if("Y".equalsIgnoreCase(prompt)){
						**/
							createJobAdvertisementWithEmail(stmt, subject, message, webPage, 
							millisStartDate, millisExpiryDate, millisSentDate);
						//}
						
					}/**end of inner if() block **/						
					else
						System.out.println("This message is not a job announcement. Message will not be read.");
					
				}/**end of first outer if() block**/
				else
					System.out.println("This message is not DBWorld mailing list material. Message will not be read.");
					
				
				//Delete the read email message after. 
				/**
				//Test code-prompt user if they want to delete current email message.
				System.out.println("Do you want to delete the message? Enter 'Y' -- " + msg[i].getSubject());
				String line = reader.readLine();
				if("Y".equalsIgnoreCase(line)){
				**/
					msg[i].setFlag(Flags.Flag.DELETED, true);
					System.out.println("Message Deleted\n");
				/**}
				else
					System.out.println("Message kept-proceeding to next message\n");
				**/
					
				//If end of email list, notify user.
				if(i==0)
					System.out.println("End of messages");
         
			}/**end of forloop**/	
			
			
			//Some POP3 servers will mark read messages for deletion. To prevent this from happening
			//before the client deletes the messages themselves, issue POP3 RSET command.
			//This clears all "marked for deletion" flags. We can then explicitly delete the messages ourselves.
			prop.put("mail.pop3.rsetbeforequit", true);				
			
			//Close connection-By closing connection. All read messages (msg that have gone thru InputStream)
			//will be deleted. To prevent this, do not close store and inbox.
			/**
			inbox.close(true);
			store.close();       
    		**/
			
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
	        	// log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	// log "Cannot close Connection"
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
		
	}/**end of method receive(String username, String password)**/

	
/*
 * checkHeaderForJobAnn(Enumeration headers)
 * 
 * This method checks through every header in the message to see if it contains
 * "job/announcement". If yes, set flag to true.
 *  
 * @param: headers - list of all the headers in the message
 */
	protected boolean checkHeaderForJobAnn(Enumeration headers){
		boolean flag = false;
        int num = 0;				
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            if(h.getValue().contains("job/announcement"))
            {
            	flag = true;
            	System.out.println("job!");
            	break;
            }
            num++;
        }
		return flag;
	}
	
	
/*
 * getStartDate(Enumeration headers)
 * 
 * Assuming current email message is a DBWorld mailing list posting, this method returns the 
 * post's start-date from the X-DBWorld-Start-Date header. Else returns a null Date.
 * 	
 * @param: headers - list of all the headers in the message
 * 
 */
	protected Date getStartDate(Enumeration headers){
		int num = 0;
		Date startDate = null;
		String startDateInString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
		while (headers.hasMoreElements()){
			Header h = (Header) headers.nextElement();
			if(h.getName().contains("DBWorld-Start-Date")){
					System.out.println(h.getValue());
					startDateInString = h.getValue();
					try{
						startDate = dateFormat.parse(startDateInString);
					}
					catch (ParseException e){
						e.printStackTrace();
					}
			}
		}		
		return startDate;
		
	}
	
	
/*
 * getExpiryDate(Enumeration headers)
 * 
 * Assuming current email message is a DBWorld mailing list posting, this method returns the
 * post's expiry-date from the X-DBWorld-Deadline header. Else returns a null Date.
 * 
 * @param: headers - list of all the headers in the message
 * 	
 */
	protected Date getExpiryDate(Enumeration headers){
		int num = 0;
		Date expiryDate = null;
		String expiryDateInString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
		while (headers.hasMoreElements()){
			Header h = (Header) headers.nextElement();
			if(h.getName().contains("DBWorld-Deadline")){
					System.out.println(h.getValue());
					expiryDateInString = h.getValue();
					try{
						expiryDate = dateFormat.parse(expiryDateInString);
						}
					catch (ParseException e){
						e.printStackTrace();
					}
			}
		}		
		return expiryDate;
	}


/*
 * getLocation(Enumeration headers)
 * 
 * Assuming current email message is a DBWorld mailing list posting, this method returns the 
 * post's location field from the X-DBWorld-Location header. Else returns a null String.
 * 
 * @param: headers - list of all the headers in the message
 * 
 */
	protected String getLocation(Enumeration headers){
		int num = 0;
		String location = null;
		while (headers.hasMoreElements()){
			Header h = (Header) headers.nextElement();
			if(h.getName().contains("DBWorld-Location")){
					System.out.println(h.getValue());
					location = h.getValue();
			}
		}
		return location;
	}


/*
 * getWebPage(Enumeration headers)
 * 
 * Assuming current email message is a DBWorld mailing list posting, this method returns the
 * post's web-page field from the X-DBWorld-Web-Page header. Else returns a null String.
 * 
 * @param: headers - list of all the headers in the message
 * 	
 */
	protected String getWebPage(Enumeration headers){
		int num = 0;
		String webPage = null;
		while (headers.hasMoreElements()){
			Header h = (Header) headers.nextElement();
			if(h.getName().contains("X-DBWorld-Web-Page")){
					System.out.println(h.getValue());
					webPage = h.getValue();
			}
		}
		return webPage;
	}
	
	
/*
 * createJobAdvertisementWithEmail(Statement stmt, String subject, String message, String webPage, 
 * long millisStartDate, long millisExpiryDate, long millisSentDate)
 * 
 * Sends information obtained from email message to JobAdTable in database as new entry.
 * 
 * @param: stmt - Database statement that executes/updates queries to the Database
 * @param: subject - Subject heading of email message
 * @param: message - Content body of email message
 * @param: webPage - Web-page field heading of email message (denoted as X-DBWorld-Web-Page as heading name)
 * @param: millisStartDate - Starting date in millisecs. of event given in email message (denoted as X-DBWorld-Start-Date as heading name)
 * @param: millisExpiryDate - Expiry date in millisecs. of event given in email message (denoted as X-DBWorld-Deadline as heading name)
 * @param: millisSentDate - Date the email was sent in milliseconds. Also used to compute JobAdID	
 * 
 */
	protected void createJobAdvertisementWithEmail(Statement stmt, String subject, String message, String webPage, 
			long millisStartDate, long millisExpiryDate, long millisSentDate){
		try{
			int jobAdId = -1;
			String jobAdvertisementTitle = subject;
			String jobDescription = message;
			String jobContactInfo = webPage;
			
			if(subject != null)
				jobAdvertisementTitle = Utility.checkInputFormat(subject);
			
			if(message != null)
				jobDescription = Utility.checkInputFormat(message);
			
			if(webPage != null)
				jobContactInfo = Utility.checkInputFormat(webPage);
		
			//Add new entry with specified parameters into database
			String query = 
				("INSERT INTO tableJobAd(title, description, contactInfo, expiryDate, dateStarting, datePosted) "
						+ "VALUES " + "('" + jobAdvertisementTitle  + "','" 
						+ jobDescription + "','" + jobContactInfo + "','" 
						+ millisStartDate + "','" + millisExpiryDate + "','" + millisSentDate + "')"); 
		
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
	
}/**end of class dbworlintegration**/




