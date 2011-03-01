package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Utility;

import managers.DBManager;
import managers.EmailManager;

public class ServletProfile extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;

	public ServletProfile() {
        super();
		dbManager = DBManager.getInstance();
    }
	
	private enum EnumAction
	{ 
		displayProfile,
		updatePublicProfile,
		updateAccounttSetting,
		UNKNOWN;
	private static EnumAction getAct(String Str)//why static?
	{
	try {return valueOf(Str);}
	catch (Exception ex){return UNKNOWN;}
	}
	};
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
		String action = request.getParameter("action");
		switch(EnumAction.getAct(action)){
		
			case displayProfile:
			
				break;
			case updatePublicProfile:
				
				break;
			case updateAccounttSetting:
				
				break;
				
			default:
				System.out.print("Dont you try to hack =D");
				break;
		
		}//ENDOF SWITCH
	
	}//ENDOF processReq Func
}