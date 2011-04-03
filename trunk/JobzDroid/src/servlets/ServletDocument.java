package servlets;

import managers.AccountManager;
import managers.SystemManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import org.apache.commons.io.FileUtils;

import classes.Session;
import classes.Utility;

/**
 * Servlet implementation class ServletDocument
 */
public class ServletDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private AccountManager accManager;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDocument() {
        super();
        accManager = new AccountManager();
    }

	private enum EnumAction	{
		deleteDocument,
		listUserDocuments
	}
    
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
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if ( isMultipart ) {
			uploadFile(request, response);
		}
		else {
			String action = request.getParameter("action");
			
			String sessionKey = request.getParameter("sessionKey");
			sessionKey = Utility.checkInputFormat(sessionKey);
			Session session = accManager.getSessionByKey(sessionKey);
			
			// throw error if action is invalid
			try{
				EnumAction.valueOf(action);
			}
			catch(Exception e){
				throw new ServletException("Invalid account servlet action.");
			}
			
			switch( EnumAction.valueOf(action) ){
				// account registration
				case deleteDocument:
					if(session.checkPrivilege( response, "searcher", "poster" ) )
						deleteDocument(request, response, session);
					break;
			}
		}

	}
	
	private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		String action	= "";
		//TODO add action and redirection
		
		String message	= "ServletDocument: Upload Failed";
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		List /* FileItem + form-fields */ items = null;
		
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		// Create a new file upload handler
		
		if ( isMultipart ) {
			try {
				// Parse the request
				System.out.println("Parsing Request");
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println("Parsing error: " + e.getMessage());
			}
		}
		
	    String name;
	    String value;
		
		// Process the uploaded items
		Iterator iter = items.iterator();
//		String nextFileName = "";
		String sessionKey = "";
		
		earlyExit: {
		while (iter.hasNext()) {
			System.out.println("Process form");
		    FileItem item = (FileItem) iter.next();

		 // Process a regular form field
		    if (item.isFormField()) {
			    name = item.getFieldName();
			    value = item.getString();
			    
//			    if(name.equals("fileName")) {
//			    	nextFileName = value;
//			    }
			    // fetch the hidden sessionkey
			    if(name.equals("sessionKey")) {
			    	sessionKey = Utility.checkInputFormat(value);
			    }
			    
			    InputStream uploadedStream = item.getInputStream();
			    uploadedStream.close();
			    
		    }
		 // Process a file upload
		    else {
		    	
		    	System.out.println("Process File Upload");
		    	
		    	
		        String fieldName = item.getFieldName();
		        String fileName = item.getName();
		        String contentType = item.getContentType();
		        boolean isInMemory = item.isInMemory();
		        long sizeInBytes = item.getSize();
		        
		        Session session = accManager.getSessionByKey( sessionKey );
		        
		        if( session.checkPrivilege(response, "searcher") == false ) {
		        	return;
		        }
		        
		        // early exit if the filename is valid
		        // (only alphanumeric or - or _ or ., and at least 1 char).(alphanumeric, at least one character)
		        // regex pattern = "^[_A-Za-z0-9-\\.]+\\.+[A-Za-z0-9]+$" see SystemManager
		        if( Utility.validate(fileName, SystemManager.fileNamePattern) == false ) {
		        	message = "ServletDocument: " + fileName + " is not a valid filename for upload\n" +
		        				"filename must contain only alphanumeric, \"-\", \"_\", or \".\", and must have valid file extension";
		        	break earlyExit;
		        }
		        
		        // early exit if file extension is not of the type accepted by the system
		        if( !checkFileExtension(fileName) ) {
		        	message = "ServletDocument: " + fileName + " is not a valid file type for upload";
		        	break earlyExit;
		        }
		        
		        //check user directory exists
		        File userDirectory = getUserDirectory( session.getIdAccount() );
		        if ( userDirectory == null ) {
		        	message = "ServletDocument: System cannot create new user directory";
		        	break earlyExit;
		        }
		        
		        //check file directory size
		        long userDirectorySize = getUserDirectorySize( session.getIdAccount() );
		        
		        if( userDirectorySize != -1 ) {
		        	// check the user directory size
		        	if( (userDirectorySize + sizeInBytes)  > SystemManager.fileStorageSizeLimit ) {
		        		message = "ServletDocument: user will exceed file upload limit of " + (SystemManager.fileStorageSizeLimit / 1000000 ) + "mB";
		        		break earlyExit;
		        	}
		        }
		        
		        File uploadedFile = new File( userDirectory , fileName );
		        
		        if ( uploadedFile.createNewFile() == false ) {
		        	if( uploadedFile.exists() ) {
		        		message = "ServletDocument: " + fileName + " already exists, change the filename or delete the original from JobzDroid";
		        	}
		        	else {
		        		message = "ServletDocument: cannot write to " + fileName;
		        	}
		        	
		        	break earlyExit;
		        }
		        
		        try {
		        	System.out.println("Writing to File: " + uploadedFile.getAbsolutePath() );
		        	message = "ServletDocument: Upload Sucessful";
					item.write(uploadedFile);
					//TODO set file permission?
				} catch (Exception e) {
					Utility.logError("Write user file error: " + e.getMessage());
				}
		    }
		    
		}
		}//earlyExit:
		System.out.println(message);
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<sessionKey>" + sessionKey + "</sessionKey>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("\t<action>" + action + "</action>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
//		response.sendRedirect( SystemManager.serverBaseURL + "/searcher/index.html" + "?sessionKey=" + sessionKey );
		
	}
	
	
	private void deleteDocument(HttpServletRequest request, HttpServletResponse response, Session session) throws ServletException, IOException {

		String message = "ServletDocument: deleteDocument() did not work as intended";
		
		String fileName = request.getParameter("fileName");
		
		boolean success = false;

		
		File userDirectory = getUserDirectory( session.getIdAccount() );
		File fileToBeDeleted = new File( userDirectory, fileName );
		
		if( fileToBeDeleted.exists() ) {
			success = fileToBeDeleted.delete();
			
			if( success ) {
				message = "ServletDocument: deleted " + fileName + " from system";
			}
			else {
				message = "ServletDocument: failed to delete " + fileName + " from system";
			}
			
		}
		else {
			message = "ServletDocument: " + fileName + " does not exist on system";
		}

		System.out.println(message);
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);

	}
	
	private boolean checkFileExtension( String fileName ) {

	    String extension = getFileExtension( fileName );
	    
		for( String validExtension : SystemManager.validFileExtensions) {
			if ( extension.equalsIgnoreCase( validExtension ) ) {
				return true;
			}
		}
	    
		return false;
	}

	private String getFileExtension( String fileName ) {
		int lastDot= fileName.lastIndexOf(".");
	    String extension=fileName.substring( lastDot ,fileName.length());  
	    
	    return extension;
	}
	
	public long getUserDirectorySize( int idAccount ) {
		File userDirectory = new File( SystemManager.documentDirectory + idAccount +"/");
		
		if ( userDirectory.exists() ) {
			return FileUtils.sizeOfDirectory( userDirectory );
		}
		else {
			return -1;
		}
		
	}
	
	private static File getUserDirectory( int idAccount ) throws IOException {
		File userDirectory = new File( SystemManager.documentDirectory + idAccount +"/");
		
		if ( !userDirectory.exists() ) {
			boolean success = userDirectory.mkdirs();
			
			if ( !success ) {
				return null;
			}
		}
		
		return userDirectory;
	}
	
	public static File[] getUserFiles( int idAccount ) {
		File userDirectory = null;
		
		try {
			userDirectory = getUserDirectory( idAccount );
		} catch (IOException e) {
			System.out.println("ServletDocument: error accessing directory of user " + idAccount);
			e.printStackTrace();
		}
		
		if( userDirectory == null ) {
			return null;
		}
		else {
			return userDirectory.listFiles();
		}
		
	}
	
	public static String getXMLDocumentList( Session userSession ) {

		String fileData = "";
			
		if( userSession == null ) {
			return "";
		}
			
		File[] userfiles = getUserFiles( userSession.getIdAccount() );
			
		for( File eachFile: userfiles ) {
			BigDecimal fileSizeMB = new BigDecimal( FileUtils.sizeOf( eachFile ) );
			fileSizeMB = fileSizeMB.divide( new BigDecimal( SystemManager.bytesInMB ) );
			
			fileData = fileData.concat("\t<file " +
					"fileName=\"" + eachFile.getName() + "\" " +
					"size=\"" + (  fileSizeMB  ) + "\" " +
					">" + "</file>\n");

		}
		
		return fileData;
		
	}
	

	
	public static String getFilesXMLByOwnerID( int idOwner ) {
		
		File[] userfiles = getUserFiles( idOwner );
		String fileData = "";
		
		for( File eachFile: userfiles ) {
			BigDecimal fileSizeMB = new BigDecimal( FileUtils.sizeOf( eachFile ) );
			fileSizeMB = fileSizeMB.divide( new BigDecimal( SystemManager.bytesInMB ) );
			
			fileData = fileData.concat("\t<file " +
					"fileName=\"" + eachFile.getName() + "\" " +
					"size=\"" + (  fileSizeMB  ) + "\" " +
					"idOwner=\"" + (  idOwner  ) + "\" " +
					">" + "</file>\n");
		}
		
		return fileData;
	}
	
	//Create a progress listener
	public ProgressListener getProgressListener() {
		ProgressListener progressListener = new ProgressListener(){
			   private long megaBytes = -1;
			   public void update(long pBytesRead, long pContentLength, int pItems) {
			       long mBytes = pBytesRead / 1000000;
			       if (megaBytes == mBytes) {
			           return;
			       }
			       megaBytes = mBytes;
			       System.out.println("We are currently reading item " + pItems);
			       if (pContentLength == -1) {
			           System.out.println("So far, " + pBytesRead + " bytes have been read.");
			       }
			       else {
			           System.out.println("So far, " + pBytesRead + " of " + pContentLength
			                              + " bytes have been read.");
			       }
			   }
		};
		return progressListener;
	}	
}