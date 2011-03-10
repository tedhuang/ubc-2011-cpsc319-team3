package servlets;

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

/**
 * Servlet implementation class ServletDocument
 */
public class ServletDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDocument() {
        super();
        // TODO Auto-generated constructor stub
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
		
		List /* FileItem */ items = null;
		
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

		//TODO check user via sessionKey
		//TODO parse filename
		//TODO check size, find entry in DB
		//TODO Write entry in DB
		
		
	    String name;
	    String value;
		
		// Process the uploaded items
		Iterator iter = items.iterator();
		String nextFileName="default";
		String sessionKey;
		
		while (iter.hasNext()) {
			System.out.println("Process form");
		    FileItem item = (FileItem) iter.next();

		 // Process a regular form field
		    if (item.isFormField()) {
			    name = item.getFieldName();
			    value = item.getString();
			    
			    if(name.equals("fileName1")) {
			    	nextFileName = value;
			    }
			    // is this where field is?
			    if(name.equals("sessionKey")) {
			    	sessionKey = value;
			    	System.out.println( sessionKey );
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
		        
		        File uploadedFile = new File( "/JobzDroid/Documents/", nextFileName );
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
