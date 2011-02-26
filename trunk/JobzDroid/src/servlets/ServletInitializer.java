package servlets;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
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
        systemManager = SystemManager.getInstance();
    }

	/**
	 * Calls SystemManager to load the configuration file to update the system variables, and then sets up timers to run automated tasks.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// load configuration file
		String realConfigPath = getServletContext().getRealPath("/WEB-INF/config.ini");
		systemManager.loadConfigFile(realConfigPath);
	}

}
