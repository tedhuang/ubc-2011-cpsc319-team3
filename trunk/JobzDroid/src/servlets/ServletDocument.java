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
		        	//TODO add error message
		        	break earlyExit;
		        }
		        
		        if ( !currSession.checkPrivilege("searcher")) {
		        	//TODO add error message
		        	break earlyExit;
		        }
		        
		        
		        // early exit if file extension is not of the type accepted by the system
		        if( !checkFileExtension(fileName) ) {
		        	//TODO add return error message
		        	break earlyExit;
		        }
		        
		        //TODO check file directory size
		        
		        long userDirectorySize = getUserDirectorySize( currSession.getIdAccount() );
		        
		        File userDirectory = new File( SystemManager.documentDirectory + SystemManager.documentDirectory );
		        
		        if( userDirectorySize != -1 ) {
		        	
		        	if(userDirectory.createNewFile() ) {
		        		System.out.print("ServletDocument: new directory created for user " + currSession.getIdAccount());
		        	}
		        	else {
		        		//TODO add error message
		        		break earlyExit;
		        	}
		        }
		        else {
		        	// check the user directory size
		        	if( (userDirectorySize + sizeInBytes)  > SystemManager.fileStorageSizeLimit ) {
		        		//TODO add error message
		        		break earlyExit;
		        	}
		        	
		        }
		        
		        
		        File uploadedFile = new File( userDirectory , nextFileName );
		        
		        if ( !uploadedFile.createNewFile() ) {
		        	//TODO add return error message
		        	break earlyExit;
		        }
		        
		        try {
		        	System.out.println("Writing to File: " + uploadedFile.getAbsolutePath() );
					item.write(uploadedFile);
					//TODO set file permission?
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    
		    
		    
		}
		}
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
		File userDirectory = new File( SystemManager.documentDirectory + SystemManager.documentDirectory );
		
		if ( userDirectory.exists() ) {
			return FileUtils.sizeOfDirectory( userDirectory );
		}
		else {
			return -1;
		}
		
	}
}
