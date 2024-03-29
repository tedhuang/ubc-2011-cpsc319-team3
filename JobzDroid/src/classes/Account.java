package classes;

public class Account {
	private int idAccount;
	private String email;
	private String secondaryEmail;
	private String type;
	private String status;
	private long dateTimeCreated;
	private String passwordMd5;
	
	public Account(int idAccount, String email, String secondaryEmail, String type, String status, long dateTimeCreated, String passwordMd5){
		this.idAccount = idAccount;
		this.email = email;
		this.secondaryEmail = secondaryEmail;
		this.type = type;
		this.status = status;
		this.dateTimeCreated = dateTimeCreated;
		this.passwordMd5 = passwordMd5;
	}
	
	public int getIdAccount(){
		return idAccount;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getSecondaryEmail(){
		return secondaryEmail;
	}
	
	public String getType(){
		return type;
	}
	
	public String getStatus(){
		return status;
	}
	
	public long getDateTimeCreated(){
		return dateTimeCreated;
	}
	
	public String getPasswordMd5(){
		return passwordMd5;
	}
}
