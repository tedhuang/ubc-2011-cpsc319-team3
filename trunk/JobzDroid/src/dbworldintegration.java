import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.htmlparser.Parser;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.jsoup.Jsoup;

import sun.net.www.URLConnection;



public class dbworldintegration {
    public static void main(String[] args) throws Exception {


	String inputLine;
	inputLine= Jsoup.connect("http://www.cs.wisc.edu/dbworld/browse.html").get().html();
	System.out.println(inputLine);
	Parser parser = new Parser();
	parser.setInputHTML(inputLine);
	parser.setEncoding("UTF-8");
	NodeList n1 = parser.parse(null);
	NodeList trs = n1.extractAllNodesThatMatch(new TagNameFilter("tr"),true);
	for(int i=0;i<trs.size();i++) {
	    NodeList nodes = trs.elementAt(i).getChildren();
	    NodeList tds  = nodes.extractAllNodesThatMatch(new TagNameFilter("td"),true);
	    // Do stuff with tds
	    RegexFilter filter = new RegexFilter("job ann.");
	    NodeList tdsfilter = tds.extractAllNodesThatMatch(filter, true);
	    System.out.println(tdsfilter.asString());
	}


	/**while ((inputLine = in.readLine()) != null){
			parser.setInputHTML(inputLine);
			parser.setEncoding("UTF-8");
		    System.out.println(inputLine);
			
	}**/
	

    }
}


