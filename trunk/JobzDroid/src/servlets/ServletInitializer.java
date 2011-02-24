package servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import managers.SystemManager;

/**
 * Servlet implementation class ServletInitializer
 * This servlet is run on server startup. Loads variables from configuration file and starts timers for automated tasks.
 */
public class ServletInitializer extends HttpServlet {	
	private static final long serialVersionUID = 1L;
	SystemManager systemManager;
       
    public ServletInitializer() {
        super();
        systemManager = new SystemManager();
    }

	/**
	 * Calls SystemManager to load the configuration file to update the system variables, and then sets up timers to run automated tasks.
	 */
	public void init(ServletConfig config) throws ServletException {
		systemManager.loadConfigFile("../../configuration");
		
	}

}
