package classes;

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
}
