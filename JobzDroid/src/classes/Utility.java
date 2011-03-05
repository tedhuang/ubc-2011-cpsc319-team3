package classes;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Utility library for other java classes
 */
public class Utility {
	
	private enum strMonths {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sept, Oct, Nov, Dec
	}
	
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
	
	
	/**
	 * Helper function to calculate time in milli seconds given year, month and day
	 */
	public static long calculateDate(int year, String month, int day){
		
		long resultTime = -1;
		int monthNum = -1;
		Calendar cal = Calendar.getInstance();
		
		//Convert string month representation to numerical representation
		switch( strMonths.valueOf(month) ){
		case Jan: monthNum = Calendar.JANUARY;
		case Feb: monthNum = Calendar.FEBRUARY;
		case Mar: monthNum = Calendar.MARCH;
		case Apr: monthNum = Calendar.APRIL;
		case May: monthNum = Calendar.MAY;
		case Jun: monthNum = Calendar.JUNE;
		case Jul: monthNum = Calendar.JULY;
		case Aug: monthNum = Calendar.AUGUST;
		case Sept: monthNum = Calendar.SEPTEMBER;
		case Oct: monthNum = Calendar.OCTOBER;
		case Nov: monthNum = Calendar.NOVEMBER;
		case Dec: monthNum = Calendar.DECEMBER;
		}
		//Calculate the number of days in that specific month
		cal.set(year, monthNum, day);

		//Calculate the total time in milliseconds (starting at unix time)
		resultTime = cal.getTimeInMillis();
		
		System.out.println("Result time: " + resultTime);
		
		return resultTime;
	}
    
	
	
	
	/***
	 * Find or create a logger for logging errors. If a logger has already been created with the name "JobzDroid", then it is returned.
	 * Otherwise a new logger is created. 
	 * @return Logger object.
	 */
	public static void logError(String errorMsg){
		Logger logger = Logger.getLogger("JobzDroid.error");
		try {
	        FileHandler fh = new FileHandler("Errors.log", true);
	        fh.setFormatter(new SimpleFormatter());
	        logger.addHandler(fh);
	        logger.severe(errorMsg);
	        
	        fh.flush();
	        fh.close();
	        logger.removeHandler(fh);
	      }
	      catch (IOException e) {
	        System.out.println("Failed to get log error: " + e.getMessage());
	      }
	}
	
	/****
	 * Converts a date string into a long.
	 * @param dateString Date string with format yyyy/MM/dd
	 * @return A long integer representing the date.
	 */
	public static long dateStringToLong(String dateString){
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd"); 
		long dateLong = -1;
		try { 
			Date date = df.parse(dateString);
			dateLong = date.getTime();
		} 
		catch (ParseException e) { 
			logError("Failure while converting date string to long: " + e.getMessage()); 
		} 
		return dateLong;
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
	
	public static String md5(String input){
        String res = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] md5 = algorithm.digest();
            String tmp = "";
            for (int i = 0; i < md5.length; i++) {
                tmp = (Integer.toHexString(0xFF & md5[i]));
                if (tmp.length() == 1) {
                    res += "0" + tmp;
                } else {
                    res += tmp;
                }
            }
        } catch (NoSuchAlgorithmException ex) {}
        return res;
    }
}
