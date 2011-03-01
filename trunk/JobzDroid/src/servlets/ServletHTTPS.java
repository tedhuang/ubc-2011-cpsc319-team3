package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.DBManager;

/**
 * Servlet implementation class ServletHTTPS
 */
public class ServletHTTPS extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    //Add Manager Creation here
    DBManager dbm = DBManager.getInstance();
    
//****************************************
//TODO: find out how to call multiple managers without having to pass all of them through parameters    
//****************************************    
    
    	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletHTTPS() {
        super();
    }
    
    
    
	/**s
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		
	}

}














