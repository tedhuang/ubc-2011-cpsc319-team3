package servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import managers.SystemManager;

/**
 * Servlet implementation class ServletInitializer
 */
public class ServletInitializer extends HttpServlet {	
	private static final long serialVersionUID = 1L;
	SystemManager systemManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletInitializer() {
        super();
        systemManager = new SystemManager();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
	}

}
