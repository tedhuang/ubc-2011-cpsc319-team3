package classes;

public class Document{
	private int idDoc;
	private int idAccount;
	private String docPath;
	private String docName;
	private long dateTimeUploaded;
	
	public Document(int idDoc, int idAccount, String docPath, String docName, long dateTimeUploaded){
		this.idDoc = idDoc;
		this.idAccount = idAccount;
		this.docPath = docPath;
		this.docName = docName;
		this.dateTimeUploaded = dateTimeUploaded;
	}
	
	public int getIdDoc(){
		return idDoc;
	}
	
	public int getIdAccount(){
		return idAccount;
	}
	
	public String getDocPath(){
		return docPath;
	}
	
	public String getDocName(){
		return docName;
	}
	
	public long getDateTimeUploaded(){
		return dateTimeUploaded;
	}
}
