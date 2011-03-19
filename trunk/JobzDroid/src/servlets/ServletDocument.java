package servlets;

import managers.DBManager;
import managers.SystemManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import org.apache.commons.io.FileUtils;

import classes.Session;

/**
 * Servlet implementation class ServletDocument
 */
public class ServletDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private DBManager dbManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDocument() {
        super();
        dbManager = DBManager.getInstance();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("Entered doPost on ServletDocument");
		
		String message	= "ServletDocument: Upload Failed";
		String action	= "";
		//TODO add action and redirection
		
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	    String name;
	    String value;
		
		// Process the uploaded items
		Iterator iter = items.iterator();
		String nextFileName = "";
		String sessionKey = "";
		
		earlyExit: {
		while (iter.hasNext()) {
			System.out.println("Process form");
		    FileItem item = (FileItem) iter.next();

		 // Process a regular form field
		    if (item.isFormField()) {
			    name = item.getFieldName();
			    value = item.getString();
			    
			    if(name.equals("fileName")) {
			    	nextFileName = value;
			    }
			    // is this where field is?
			    if(name.equals("sessionKey")) {
			    	sessionKey = value;
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
		        
		        Session currSession = dbManager.getSessionByKey( sessionKey );
		        
		        if ( currSession == null ) {
		        	message = "ServletDocument: expired or invalid session";
		        	break earlyExit;
		        }
		        
		        if ( currSession.checkPrivilege("searcher")  == false) {
		        	message = "ServletDocument: invalid user type, user does not have the privilege";
		        	break earlyExit;
		        }
		        
		        
		        // early exit if file extension is not of the type accepted by the system
		        if( !checkFileExtension(fileName) ) {
		        	message = "ServletDocument: " + fileName + " is not a valid file type for upload";
		        	break earlyExit;
		        }
		        
		        
		        //TODO check user directory exists
		        File userDirectory = getUserDirectory( currSession.getIdAccount() );
		        if ( userDirectory == null ) {
		        	message = "ServletDocument: System cannot create new user directory";
		        	break earlyExit;
		        }
		        
		        //TODO check file directory size
		        long userDirectorySize = getUserDirectorySize( currSession.getIdAccount() );
		        
		        if( userDirectorySize != -1 ) {
		        	// check the user directory size
		        	if( (userDirectorySize + sizeInBytes)  > SystemManager.fileStorageSizeLimit ) {
		        		message = "ServletDocument: user will exceed file upload limit of " + (SystemManager.fileStorageSizeLimit / 1000000 ) + "mB";
		        		break earlyExit;
		        	}
		        }
		        
		        
		        File uploadedFile = new File( userDirectory , nextFileName );
		        
		        if ( uploadedFile.createNewFile() == false ) {
		        	message = "ServletDocument: System cannot write to file " + nextFileName;
		        	break earlyExit;
		        }
		        
		        try {
		        	System.out.println("Writing to File: " + uploadedFile.getAbsolutePath() );
		        	message = "ServletDocument: Upload Sucessful";
					item.write(uploadedFile);
					//TODO set file permission?
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		}
		}
		
		System.out.println(message);
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
	
	private File getUserDirectory( int idAccount ) throws IOException {
		File userDirectory = new File( SystemManager.documentDirectory + idAccount +"/");
		
		if ( !userDirectory.exists() ) {
			boolean success = userDirectory.mkdirs();
			
			if ( !success ) {
				return null;
			}
		}
		
		return userDirectory;
	}
}
