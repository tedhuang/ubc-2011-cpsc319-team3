package managers;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import classes.Utility;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

/***
 * Manages creating, reading and editing RSS 2.0 feeds.
 */
public class RSSManager {	
	/***
	 * Creates a SyndEntry(ROME) object (RSS feed entry object) with provided parameters.
	 * @param title Feed entry title.
	 * @param pubDate Feed entry publish date.
	 * @param description Feed entry description.
	 * @return SyndEntry object.
	 */
	public static SyndEntry createFeedEntry(String title, Date pubDate, String description){
		SyndEntry entry = new SyndEntryImpl();
		entry.setTitle(title);
		entry.setPublishedDate(pubDate);
		
        SyndContent desc = new SyndContentImpl();
        desc.setType("text/html");
        desc.setValue(description);
        entry.setDescription(desc);   
		return entry;
	}
	
	/***
	 * Creates a SyndEntry(ROME) object (RSS feed entry object) with provided parameters.
	 * @param title Feed entry title.
	 * @param link Feed entry link.
	 * @param pubDate Feed entry publish date.
	 * @param description Feed entry description.
	 * @param categories Array of feed categories as Strings.
	 * @return SyndEntry object.
	 */
	public static SyndEntry createFeedEntry(String title, Date pubDate, String description,  String link, String[] categories){
		SyndEntry entry = createFeedEntry(title, pubDate, description);	
		if(link != null)
			entry.setLink(link);
        if(categories != null){
	        List<SyndCategory> catagoryList = new ArrayList<SyndCategory>();
	        for(int i = 0; i < categories.length; i++){
	        	SyndCategory category = new SyndCategoryImpl();
	            category.setName(categories[i].trim());
	            catagoryList.add(category);
	        }
	        entry.setCategories(catagoryList);        
        }
		return entry;
	}
	
	/***
	 * Creates a SyndFeed(ROME) object (RSS feed object) with provided parameters.
	 * @param title Feed title.
	 * @param link Feed link.
	 * @param description Feed description.
	 * @param entries List of feed entries.
	 * @return SyndFeed object.
	 */
	public static SyndFeed createFeed(String title, String link, String description, List<SyndEntry> entries){
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle(title);
        feed.setLink(link);
        feed.setDescription(description);
        feed.setEntries(entries);
		return feed;
	}
	
	/***
	 * Writes the given feed to a file in xml format.
	 * @param feed Input feed.
	 * @param filename File to write to.
	 */
	public static void writeFeedToFile(SyndFeed feed, String filepath){
		try{
			Writer writer = new FileWriter(filepath);
	        SyndFeedOutput output = new SyndFeedOutput();
	        output.output(feed,writer);
	        writer.close();
		}
		catch(Exception e){
			Utility.logError("Error writing feed: " + e.getMessage());
		}
	}
	
	/***
	 * Read feed information from the given file, and then write them into a SyndFeed object.
	 * @param fileURL URL of the feed file.
	 * @return SyndFeed object.
	 * @throws IOException 
	 * @throws FeedException 
	 * @throws IllegalArgumentException 
	 */
	public static SyndFeed readFeedFromURL(String fileURL) throws IllegalArgumentException, FeedException, IOException{
		SyndFeed feed;
		URL feedSource = new URL(fileURL);
	    SyndFeedInput input = new SyndFeedInput();
	    feed = input.build(new XmlReader(feedSource));
		return feed;
	}
	
	/***
	 * Adds the given entry as the latest entry(at the beginning of the entry list) in the given feed.
	 * @param feed Feed to add entry into.
	 * @param entry Entry being added to the feed.
	 * @param index Position in the entry list where the entry is being added into.
	 * @return Feed with new latest entry. 
	 */
	public static SyndFeed addEntryToFeed(SyndFeed feed, SyndEntry entry, int index){
		SyndFeed outFeed = new SyndFeedImpl();
		outFeed.setFeedType(feed.getFeedType());
		outFeed.setTitle(feed.getTitle());
		outFeed.setLink(feed.getLink());
		outFeed.setDescription(feed.getDescription());
        
        @SuppressWarnings("unchecked")
		List<SyndEntry> entries = feed.getEntries();
        entries.add(index, entry);
        outFeed.setEntries(entries);        
        return outFeed;
	}
	
	/***
	 * Removes the feed entry with given index from the feed.
	 * @param feed Feed to delete entry from.
	 * @param index Index of the entry to delete.
	 * @return Feed with specified entry removed.
	 */
	public static SyndFeed removeEntryFromFeed(SyndFeed feed, int index){
		SyndFeed outFeed = new SyndFeedImpl();
		outFeed.setFeedType(feed.getFeedType());
		outFeed.setTitle(feed.getTitle());
		outFeed.setLink(feed.getLink());
		outFeed.setDescription(feed.getDescription());
        
        @SuppressWarnings("unchecked")
		List<SyndEntry> entries = feed.getEntries();
        entries.remove(index);
        outFeed.setEntries(entries);        
        return outFeed;
	}
	
	/***
	 * Searches the given feed for an entry with the given title, and content. Returns the index of the feed entry.
	 * @param feed Feed to search from.
	 * @param title Title of the entry to search for.
	 * @param content Content of the entry to search for.
	 * @return Index of the first matching feed entry. -1 if none found.
	 */
	public static int searchEntry(SyndFeed feed, String title, String content, Date dateTimePublished){
		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = feed.getEntries();
		Iterator<SyndEntry> itr = entries.iterator(); 
		int currIndex = 0;
		while(itr.hasNext()) {
			SyndEntry entry = itr.next();
			String entryTitle = entry.getTitle();
			String entryDesc = entry.getDescription().getValue();
			Date entryPublishedTime = entry.getPublishedDate();
			if( title.equals(entryTitle) && content.equals(entryDesc) && dateTimePublished.equals(entryPublishedTime) )
				return currIndex;
			else
				currIndex++;
		} 
		return -1;
	}
}