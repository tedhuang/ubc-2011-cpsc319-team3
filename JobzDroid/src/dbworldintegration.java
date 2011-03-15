import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
//MessageCountListener-when inbox gets new email invoke method to read new email
//Check message subject heading, find this heading in the website and see if this dbworld news is a "job ann." news.
//If so, update to database.

public class dbworldintegration{
	 
public static void main(String[] args)
{
	String user="jobzdroidtestemail@gmail.com";
    String passwd="cpsc319team3";
	new dbworldintegration().receive(user,passwd);
}


public void receive(String username,String password) 
{
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
        
        String inputLine;
        inputLine= Jsoup.connect("http://www.cs.wisc.edu/dbworld/browse.html").get().html();
        Parser parser = new Parser();
        parser.setInputHTML(inputLine);
        parser.setEncoding("UTF-8");
        NodeList n1 = parser.parse(null);
        NodeList trs = n1.extractAllNodesThatMatch(new TagNameFilter("tr"),true);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (int i = msg.length-1; i >= 0; i--) {
          String getReplyTo = msg[i].getReplyTo()[0].toString();	
          
          String getSubject = msg[i].getSubject();
          getSubject = getSubject+" ";
          //invoke searchDBWorldAnnPostings();
          boolean flagSuccess = searchDBWorldAnnPosting(trs, getSubject);
/*          
          if(getReplyTo.contentEquals("dbworld_owner@yahoo.com") && flagSuccess)
          {	  
        	  System.out.println("From : " + msg[i].getFrom()[0]);
        	  System.out.println("Reply to : "+ msg[i].getReplyTo()[0]);
        	  System.out.println("Subject : " + msg[i].getSubject());
        	  System.out.println("Date:" + msg[i].getSentDate()); 

        	  System.out.print("Message : ");
        	  InputStream stream = msg[i].getInputStream();
        	  while (stream.available() != 0) {
        		  System.out.print((char) stream.read());
        	  }
        	  System.out.println();
        	  System.out.println("-----------------------------------------------------");

          }//end of if statement ^
*/          
     	 //Todo: Delete the email
         System.out.println("Do you want to delete the message? -- " + msg[i].getSubject());
         String line = reader.readLine();
         if("Y".equalsIgnoreCase(line)){
        	 msg[i].setFlag(Flags.Flag.DELETED, true);
        	 System.out.println("Msg Deleted\n");
         }
         else
        	 System.out.println("Msg kept-proceeding to next msg\n");


          /**else if ("N".equalsIgnoreCase(line))
          {
          	if (inbox != null) {
                inbox.close(true);
            	}
            	if (store != null) {
                store.close();
            	}
          }
          //else
          {
        	if (inbox != null) {
            inbox.close(true);
        	}
        	if (store != null) {
            store.close();
        	}
          }**/
        }//end of forloop
        
        //Close connection
        inbox.close(true);
        store.close();
        
    }//end of try block
    catch (Exception e) {
        e.printStackTrace();
    }
    
 }//end of method receive(String username, String password)

/**
 * " searchDBWorldAnnPosting(NodeList trs, String msgSubject) "
 * 
 * @param: NodeList trs - a list of nodes that contain html table rows
 * @param: String 	msgSubject - email message Subject Heading
 * This method sees if given email is a "job ann" posting in DbWorld website.
 *
 **/
public boolean searchDBWorldAnnPosting(NodeList trs, String msgSubject){
	boolean flag=false;
	try{
System.out.println("searchDBWorldAnnPosting Method Entered");
		/**
		String inputLine;
		inputLine= Jsoup.connect("http://www.cs.wisc.edu/dbworld/browse.html").get().html();
		Parser parser = new Parser();
		parser.setInputHTML(inputLine);
		parser.setEncoding("UTF-8");
		NodeList n1 = parser.parse(null);
		NodeList trs = n1.extractAllNodesThatMatch(new TagNameFilter("tr"),true);
		**/
		for(int i=1;i<=10;i++) {
			NodeList nodes = trs.elementAt(i).getChildren();
			NodeList tds  = nodes.extractAllNodesThatMatch(new TagNameFilter("td"),true);

			NodeList annNode = tds.elementAt(1).getChildren();
			String annNodeToString = annNode.asString();

			NodeList subjectNode = tds.elementAt(3).getChildren();
			String subjectNodeToString = subjectNode.asString();
			
//System.out.println("msgSubject:"+msgSubject);
//System.out.println("subjectNodeToString:"+subjectNodeToString);
//System.out.print("success point\n");
			//Do stuff with annNode

			if(msgSubject.contains(subjectNodeToString) && annNodeToString.contains("job ann."))
			{
System.out.print("FLAG SET TO BE TRUEEEEEE\n");
				flag=true;
				break;
			}
			else
				flag=false;
		} 
	}//end of try block
    catch (Exception e) {
    	System.out.println("FOOOL");
        e.printStackTrace();
    }    	
    return flag;
}//end of searchDBWorld()
	

}//end of class dbworlintegration




