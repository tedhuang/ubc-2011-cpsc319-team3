package classes;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Utility library for other java classes
 */
public class Utility {
	/***
	 * Determines whether a given string matches a regular expression pattern.
	 * @param string String to be validated.
	 * @param pattern Regular expression pattern to be used to validate the string.
	 * @return boolean indicating whether the string matches the pattern.
	 */
	public static boolean validate(final String string, final String pattern){
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(string);
		return matcher.matches();
	}
	
	/***
	 * Gets the current date and time in the form of a long integer.
	 * @return long representing the current date and time
	 */
	public static long getCurrentTime(){
		return new java.util.Date().getTime();
	}
	
	/***
	 * Find or create a logger for logging errors. If a logger has already been created with the name "JobzDroid", then it is returned.
	 * Otherwise a new logger is created. 
	 * @return Logger object.
	 */
	public static Logger getErrorLogger(){
		Logger logger = Logger.getLogger("JobzDroid.error");
		try {
	        FileHandler fh = new FileHandler("Errors.log", true);
	        fh.setFormatter(new SimpleFormatter());
	        logger.addHandler(fh);
	      }
	      catch (IOException e) {
	        System.out.println("Failed to get error logger: " + e.getMessage());
	      }
	    return logger;
	}
	
	/***
	 * Corrects user SQL input to acceptable format, preventing injection attacks.
	 * @param toBeChecked String to be checked.
	 * @return The corrected string.
	 */
	public static String checkInputFormat(String toBeChecked){		
		String corrected;
		corrected = toBeChecked.replace("\'", "\\\'");
		corrected = toBeChecked.replace("\"", "\\\"");
		corrected = toBeChecked.replace(";", "");
		corrected = toBeChecked.replace("{", "");
		corrected = toBeChecked.replace("}", "");
		corrected = toBeChecked.replace("<", "");
		corrected = toBeChecked.replace(">", "");
		corrected = toBeChecked.replace("^", "");
		
		return corrected;		
	}
}
