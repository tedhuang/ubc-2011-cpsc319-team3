package servlets;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import classes.DBColName;
import managers.SystemManager;
import managers.dbworldintegration;

/**
 * Servlet implementation class ServletInitializer
 * This servlet is run on server startup. Loads variables from configuration file and starts timers for automated tasks.
 */
public class ServletInitializer extends HttpServlet {	
	private static final long serialVersionUID = 1L;
	SystemManager systemManager;
	Timer timer;
	Timer dbWorldTimer;
	dbworldintegration dbworldintegration;
    protected static DBColName DbDict; //shared DbDict, can only init for once
       
    public ServletInitializer() {
        super();
        systemManager = SystemManager.getInstance();
        dbworldintegration = new dbworldintegration();
        dbWorldTimer = new Timer();
        timer = new Timer();
        DbDict =	new DBColName(); 
    }

	/**
	 * Calls SystemManager to load the configuration file to update the system variables, and then sets up timers to run automated tasks.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// load configuration file
		String configPath = getServletContext().getRealPath("/WEB-INF/config.ini");
		systemManager.loadConfigFile(configPath);
		// schedule automated tasks
	    class AutomatedTasks extends TimerTask {
	        public void run() {
	        	systemManager.removeExpiredEmailVerifications();
	        	systemManager.removeExpiredPwResetRequests();
	        	systemManager.removeExpiredSessionKeys();
	        	systemManager.removeExpiredPendingAccounts();
	        	systemManager.removeExpiredInactiveJobAds();
	        	systemManager.makeInactiveExpiredJobAds();	        	
	        }
	    }
	    
		final String jobAdRSSPath = getServletContext().getRealPath("jobAd.xml");
	    class AutomatedTasksDBWorld extends TimerTask {
	    	public void run(){
	            dbworldintegration.emailParse(SystemManager.dbWorldEmailAddress, SystemManager.dbWorldEmailPw, jobAdRSSPath);	    		
	    	}
	    }
	    
		timer.schedule(new AutomatedTasks(), 0, SystemManager.timeIntervalAutomatedTasks);		
		dbWorldTimer.schedule(new AutomatedTasksDBWorld(), 0, SystemManager.timeIntervalAutomatedDBWorldTasks);
	}
	
	protected static DBColName retDbColName(){
		return DbDict;
	}
}
