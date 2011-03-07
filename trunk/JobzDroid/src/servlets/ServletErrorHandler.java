package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Forwards common error codes and exceptions to the error page. Can be extended for custom error handling.
 */
public class ServletErrorHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public ServletErrorHandler() {
        super();
    }

	/**
	 * Forward to error page.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("error.html");
		dispatcher.forward(request, response);
	}

}
