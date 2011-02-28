package managers;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;

import classes.Utility;

//import managers.DBManager;

//import java.sql.Array;
import java.util.*;

public class EmailManager {

	private static final String CRAIGSBAY_EMAIL = "craigsbayauctionhouse@gmail.com";
	private static final String CRAIGSBAY_EMAIL_PW = "craigsbayrocks";
	private static final String GMAIL_SMTP_HOST = "smtp.gmail.com";
	private static final String GMAIL_PORT = "465";
	
	public EmailManager() {}
	
	/***
	 * Sends an account activation email containing a link to activate the account to the to the given address.
	 * @param address Email address of the receiver.
	 * @param name Name or company/organization name of the new user receiving the account activation email.
	 * @param uuid Unique id for activating the account.
	 * @return boolean indicating whether the email was successfully sent.
	 */
	public void sendAccountActivationEmail(String address, String name, UUID uuid) {
		String emailTitle = "Jobzdroid Account Activation";
		String emailBody = "Greetings, " + name + "!\n\nThank you for registering on JobzDroid!\n" +
							"Please click on the link below to activate your account:\n" +
							"http://localhost:8080/JobzDroid/Account?action=activate&id=" + uuid +
							"\n\nRegards,\nJobzDroid Development Team\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an account primary email change conformation email containing a link
	 *  to confirm the email change to the given address.
	 * @param address Email address of the receiver.
	 * @param uuid Unique id for confirming the email change request.
	 * @return boolean indicating whether the email was successfully sent.
	 */
	public void sendPrimaryEmailChangeVerificationEmail(String address, UUID uuid){
		String emailTitle = "Jobzdroid Primary Email Change Confirmation";
		String emailBody = "Greetings!\n\n" +
							"Please click on the link below to confirm your primary email change:\n" +
							"http://localhost:8080/JobzDroid/Account?action=verifyEmailChange&id=" + uuid +
							"\n\nRegards,\nJobzDroid Development Team\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends a forget password reset request email to the to the given address.
	 * Contains a link to reset the password.
	 * @param address Email address of the receiver.
	 * @param uuid Unique id for resetting password.
	 * @return boolean indicating whether the email was successfully sent.
	 */
	public void sendPasswordResetEmail(String address, UUID uuid){
		String emailTitle = "Jobzdroid Password Reset Request";
		String emailBody = "Greetings!\n\n" +
							"Please click on the link below to reset your password:\n" +
							"http://localhost:8080/JobzDroid/Account?action=emailLinkForgetPassword&id=" + uuid +
							"\n\nRegards,\nJobzDroid Development Team\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an email to the specified email address with the provided title and message body.
	 * @param address Email address of the receiver.
	 * @param title Title of the email.
	 * @param body Message body of the email.
	 * @return boolean indicating whether the email was successfully sent.
	 */
	private void sendEmail(String address, String title, String body) {
		Properties properties = new Properties();
		properties.put("mail.smtp.user", CRAIGSBAY_EMAIL);
		properties.put("mail.smtp.host", GMAIL_SMTP_HOST);
		properties.put("mail.smtp.port", GMAIL_PORT);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.debug", "true");
		properties.put("mail.smtp.socketFactory.port", GMAIL_PORT);
		properties.put("mail.smtp.socketFactory.class",	"javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
//		SecurityManager security = System.getSecurityManager();

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(properties, auth);
	//		session.setDebug(true);

			MimeMessage msg = new MimeMessage(session);
			msg.setText(body);
			msg.setSubject(title);
			msg.setFrom(new InternetAddress(address));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
			Transport.send(msg);
		} 
		catch (Exception e) {
			// log error
			Utility.getErrorLogger().severe("Failed to send email to " + address + " : " + e.getMessage()); 
		}
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(CRAIGSBAY_EMAIL, CRAIGSBAY_EMAIL_PW);
		}
	}
}

