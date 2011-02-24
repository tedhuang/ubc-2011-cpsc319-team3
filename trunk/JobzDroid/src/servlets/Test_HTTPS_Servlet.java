package servlets;

/* 
 *  This material contains programming source code for your 
 *  consideration.  These examples have not been thoroughly 
 *  tested under all conditions.  IBM, therefore, cannot 
 *  guarantee or imply reliability, serviceability, or function 
 *  of these program.  All programs contained herein are 
 *  provided to you "AS IS".  THE IMPLIED WARRANTIES OF 
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 *  ARE EXPRESSLY DISCLAIMED.  IBM provides no program services for 
 *  these programs and files. 
 */ 
import java.io.DataInputStream; 
import java.net.URLConnection; 
import java.net.URL; 
import java.net.URLDecoder; 
import java.io.PrintWriter; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
  

public class Test_HTTPS_Servlet extends HttpServlet 
{ 
	private static final long serialVersionUID = 1L;

public void doGet(HttpServletRequest req, HttpServletResponse res) { 
  
    res.setContentType("text/html"); 
  
    // url passed in as browser query string 
    String url  = req.getParameter("httpsURL"); 
    if (null != url) 
      url = URLDecoder.decode(url); 
    else { 
      // url passed in as servlet init parameter 
      url = getInitParameter("httpsURL"); 
    } 

    URLConnection conn = null; 
    URL connectURL = null; 
  
    // send result to the caller 
    try { 

      PrintWriter out = res.getWriter(); 
      if (null == url || url.length() == 0) { 
        out.println("No Https URL provided to retrieve"); 
      } 
        else { 
          connectURL = new URL(url); 
          conn = connectURL.openConnection(); 
          DataInputStream theHTML = new DataInputStream(conn.getInputStream()); 
          String thisLine; 
          while ((thisLine = theHTML.readLine()) != null) { 
            out.println(thisLine); 
          } 
        } 
        out.flush(); 
        out.close(); 
      }
    catch (Exception e) { 
      System.out.println("Exception in HttpsSampleServlet: " + e.getMessage()); 
      e.printStackTrace(); 
    } 
  }//end goGet(...)
}//end class