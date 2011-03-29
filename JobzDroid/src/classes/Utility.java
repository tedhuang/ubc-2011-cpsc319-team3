package classes;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
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
	
	/**
	 * Escapes the following characters into valid XML: &, < , >, ', " 
	 * @param text Input text.
	 * @return Processed text.
	 */
	public static String processXMLEscapeChars(String text){
		if(text != null){
			text = text.replaceAll("&", "&amp;");
			text = text.replaceAll("<", "&lt;");
			text = text.replaceAll(">", "&gt;");
			text = text.replaceAll("\"", "&quot;");
			text = text.replaceAll("'", "&apos;");
		}
		return text;
	}
	
	/***
	 * Converts string line breaks into HTML line break tags, and two or more white spaces into a sequence of NBSP.
	 * @param input Input string.
	 * @return Processed string.
	 */
	public static String processLineBreaksWhiteSpaces(String input){
		Pattern p = Pattern.compile("(\r\n|\r|\n|\n\r)");
		Matcher m = p.matcher(input);			 
		if (m.find()) {
			input = m.replaceAll("<br />");
		}
		input = input.replace("  ", "&nbsp;&nbsp;");
		input = input.replace("&nbsp; ", "&nbsp;&nbsp;"); 
		return input;
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
	 * Converts a long to a formatted date string.
	 * @param time Time represented as a long.
	 * @param timeZone Time zone string. For example "GMT" or "PST".
	 * @return Formated date string. For example "Thursday, 10 March 2011, 23:20:59 PST"
	 */
	public static String longToDateString(long time, String timeZone){
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		c.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE, d MMMMMMMM yyyy, HH:mm:ss z");
		return sdf.format(c.getTime());
	}
	
	/***
	 * Corrects user SQL input element to an acceptable format, preventing injection attacks.
	 * @param toBeChecked String to be checked.
	 * @return The corrected string.
	 */
	public static String checkInputFormat(String toBeChecked){		
		String corrected="";
		try{
			corrected = toBeChecked.replace("\'", "\\\'");
			corrected = toBeChecked.replace("\"", "\\\"");
			corrected = toBeChecked.replace(";", "");
			corrected = toBeChecked.replace("{", "");
			corrected = toBeChecked.replace("}", "");
			corrected = toBeChecked.replace("<", "");
			corrected = toBeChecked.replace(">", "");
			corrected = toBeChecked.replace("^", "");
			corrected = toBeChecked.replace("'", "\\'");
		}
		catch(NullPointerException e){
			Utility.logError("Null value found when processing input data.");
			return null;
		}
		return corrected;		
	}
	
	public static String checkInputFormat(String toBeChecked, String mode){		
		String corrected="";
		
		if(mode.equals("draft")){
			corrected = toBeChecked.replace("\'", "\\\'");
			corrected = toBeChecked.replace("\"", "\\\"");
			corrected = toBeChecked.replace(";", "");
			corrected = toBeChecked.replace("{", "");
			corrected = toBeChecked.replace("}", "");
			corrected = toBeChecked.replace("<", "");
			corrected = toBeChecked.replace(">", "");
			corrected = toBeChecked.replace("^", "");
			corrected = toBeChecked.replace("'", "\\'");
		}
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
	
	
/*******************************************************************************************************************
 * 							Date Convertion Function Group
 *******************************************************************************************************************/
	public static String dateConvertor(long milSecDate){
		
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milSecDate);
        return formatter.format(calendar.getTime());
	}
//////////////////////////////////////////////////////////////////////////////////
	public static long dateConvertor(String date){
	  boolean badFormat=true;
	  boolean badTime =true;
	  long dateInMs=0;
	  
	  Calendar minCal=Calendar.getInstance();
	  minCal.add(Calendar.MONTH, 1);
	  Calendar maxCal=Calendar.getInstance();
	  maxCal.add(Calendar.MONTH, 3);
	  
	  if(date!=null){
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
		StringTokenizer st=new StringTokenizer(date, "/");
		if(st.countTokens()==3){//we know the delimiters format is right
			try { 
				Date aDate = df.parse(date);
				badFormat=false;
				dateInMs = aDate.getTime();
				if(dateInMs > minCal.getTimeInMillis()|| dateInMs<maxCal.getTimeInMillis() ){
					badTime=false;
				}
			} 
			catch (ParseException e) { 
				logError("Failure while converting date string to long: " + e.getMessage()); 
			}
		}
	    if(badFormat || badTime){//some other bad format, we do a default setting: one month
	         dateInMs=minCal.getTimeInMillis();
	    }
	  }
	  return dateInMs;
	}
/******************ENDOF Date Convention********************************************************/
	
	public static String degreeConvertor(int numForm){
		
		switch (numForm){
			case 1:
				return "B.Sc.";
			case 2:
				return "M.Sc.";
			case 3:
				return "Ph.D.";
			default:
				return "Not Specified";
		}
	}	
	
	public static int degreeConvertor(String input){//TODO ADD REGEXP TO IT
		
		if(input.equalsIgnoreCase("Ph.D.")){
			return 3;
		}
		else if(input.equalsIgnoreCase("B.Sc.")|| input.equalsIgnoreCase("bachelor")){
			return 1;
		}
		else if(input.equalsIgnoreCase("M.Sc.")|| input.equalsIgnoreCase("master")){
			return 2;
		}
		else{
			return -1;
		}
	}
	
	public static String jobTypeTranslator(boolean intoDB, String input){
	  if(input!=null){
		  
		StringBuffer strBuf =new StringBuffer();
		if(!intoDB){
			if(input.equalsIgnoreCase("fulltime")||input.equalsIgnoreCase("parttime")){
		        input = input.substring(0,1).toUpperCase() + input.substring(1).toLowerCase();
				strBuf.append(input).insert(4, " ");
				return strBuf.toString();
			}
			else{
					return input;
			}
		}
		
		else if( input.equalsIgnoreCase("full time")|| input.equalsIgnoreCase("part time")){
				   String str = input.toLowerCase().replaceAll(" t", "T");
				   return str;
				 }
			 
		else if(input.equalsIgnoreCase("internship")){
					return input;
			}
	  }
		return "unknown";
	 }	
}//ENDOF UTILITY CLASS