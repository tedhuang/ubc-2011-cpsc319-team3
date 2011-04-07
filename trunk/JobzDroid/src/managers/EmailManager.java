package managers;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;

import classes.Utility;

//import managers.DBManager;

//import java.sql.Array;
import java.util.*;

/***
 * Provides functionalities for sending emails.
 */
public class EmailManager {
	public EmailManager() {}
	
	/***
	 * Sends an account activation email containing a link to activate the account to the to the given address.
	 * @param address Email address of the receiver.
	 * @param name Name or company/organization name of the new user receiving the account activation email.
	 * @param uuid Unique id for activating the account.
	 */
	public void sendAccountActivationEmail(String address, String name, UUID uuid) {
		String emailTitle = "JobzDroid Account Activation";
		String emailBody = "Greetings, " + name + "!\n\nThank you for registering on JobzDroid!\n" +
							"Please click on the link below to activate your account:\n" +
							SystemManager.serverBaseURL + "Account?action=activate&id=" + uuid +
							"\n\nRegards,\nJobzDroid Development Team\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an account primary email change conformation email containing a link
	 *  to confirm the email change to the given address.
	 * @param address Email address of the receiver.
	 * @param uuid Unique id for confirming the email change request.
	 */
	public void sendPrimaryEmailChangeVerificationEmail(String address, UUID uuid){
		System.out.println("Inside sendPrimaryEmailChangeVerificationEmail");
		String emailTitle = "JobzDroid Primary Email Change Confirmation";
		String emailBody = "Greetings,\n\n" +
							"Please click on the link below to confirm your primary email change:\n" +
							SystemManager.serverBaseURL + "Account?action=verifyEmailChange&id=" + uuid +
							"\n\nRegards,\nJobzDroid Development Team\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends a forget password reset request email to the to the given address.
	 * Contains a link to reset the password.
	 * @param address Email address of the receiver.
	 * @param uuid Unique id for resetting password.
	 */
	public void sendPasswordResetEmail(String address, UUID uuid){
		String emailTitle = "JobzDroid Password Reset Request";
		String emailBody = "Greetings,\n\n" +
							"Please click on the link below to reset your password:\n" +
							SystemManager.serverBaseURL + "Account?action=emailLinkForgetPassword&id=" + uuid +
							"\n\nRegards,\nJobzDroid Development Team\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an email informing the user about the ban.
	 * @param address Email address of the receiver.
	 * @param reason Reason of the ban.
	 */
	public void informBan(String address, String reason){
		String emailTitle = "JobzDroid Account Action";
		String emailBody = "Greetings,\n\n" +
							"Your JobzDroid account " + address + " has been banned for the following reason:\n"+
							reason +
							"\n\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an email informing the user about the unban.
	 * @param address Email address of the receiver.
	 * @param reason Reason of the unban.
	 */
	public void informUnban(String address, String reason){
		String emailTitle = "JobzDroid Account Action";
		String emailBody = "Greetings,\n\n" +
							"Your JobzDroid account " + address + " has been unbanned for the following reason:\n"+
							reason +
							"\n\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an email informing the user about the deletion.
	 * @param address Email address of the receiver.
	 * @param reason Reason of the deletion.
	 */
	public void informDeletion(String address, String reason){
		String emailTitle = "JobzDroid Account Action";
		String emailBody = "Greetings,\n\n" +
							"Your JobzDroid account " + address + " has been permanently deleted for the following reason:\n"+
							reason +
							"\n\n(Please do not reply to this message.)";
		sendEmail(address, emailTitle, emailBody);
	}
	
	/***
	 * Sends an email to the specified email address with the provided title and message body.
	 * @param address Email address of the receiver.
	 * @param title Title of the email.
	 * @param body Message body of the email.
	 */
	public void sendEmail(String address, String title, String body) {
		Properties properties = new Properties();
		properties.put("mail.smtp.user", SystemManager.systemEmailAddress);
		properties.put("mail.smtp.host", SystemManager.systemEmailSMTPHost);
		properties.put("mail.smtp.port", SystemManager.systemEmailPort);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.debug", "true");
		properties.put("mail.smtp.socketFactory.port", SystemManager.systemEmailPort);
		properties.put("mail.smtp.socketFactory.class",	"javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");

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
			Utility.logError("Failed to send email to " + address + " : " + e.getMessage()); 
		}
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(SystemManager.systemEmailAddress, SystemManager.systemEmailPw);
		}
	}
}

