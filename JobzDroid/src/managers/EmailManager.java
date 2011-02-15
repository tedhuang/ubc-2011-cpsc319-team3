package managers;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;

//import managers.DBManager;

//import java.sql.Array;
import java.util.*;

public class EmailManager {

	private static final String CRAIGSBAY_EMAIL = "craigsbayauctionhouse@gmail.com";
	private static final String CRAIGSBAY_EMAIL_PW = "craigsbayrocks";
	private static final String GMAIL_SMTP_HOST = "smtp.gmail.com";
	private static final String GMAIL_PORT = "465";
	
//	private db_manager dbm = null;
	
	public EmailManager()
	{
//		dbm = new db_manager;
	}

	
	private boolean send_mail(String address, String title, String body)
	{
		Properties properties = new Properties();
		properties.put("mail.smtp.user", CRAIGSBAY_EMAIL);
		properties.put("mail.smtp.host", GMAIL_SMTP_HOST);
		properties.put("mail.smtp.port", GMAIL_PORT);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.debug", "true");
		properties.put("mail.smtp.socketFactory.port", GMAIL_PORT);
		properties.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
//		SecurityManager security = System.getSecurityManager();

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(properties, auth);
			 session.setDebug(true);

			MimeMessage msg = new MimeMessage(session);
			msg.setText(body);
			msg.setSubject(title);
			msg.setFrom(new InternetAddress(address));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(address));
			Transport.send(msg);
			System.out.println("done");
		} catch (Exception mex) {
			mex.printStackTrace();
		}
		
		return true;
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(CRAIGSBAY_EMAIL, CRAIGSBAY_EMAIL_PW);
		}
	}
}

