package classes;

public class NewsEntry implements Comparable<NewsEntry>{
	private int idNews;
	private String title;
	private String content;
	private long dateTimePublished;
	
	public NewsEntry(int idNews, String title, String content, long dateTimePublished){
		this.idNews = idNews;
		this.title = title;
		this.content = content;
		this.dateTimePublished = dateTimePublished;
	}
	
	public int getIdNews(){
		return idNews;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContent(){
		return content;
	}
	
	public long getDateTimePublished(){
		return dateTimePublished;
	}

	@Override
	// More recently posted news are "smaller" than older news, to place them at the beginning of the entries.
	public int compareTo(NewsEntry otherNewsEntry) {
		long thisPublishTime = this.getDateTimePublished();
		long otherPublishTime = otherNewsEntry.getDateTimePublished();
		if( thisPublishTime > otherPublishTime )
			return -1;
		else if( thisPublishTime == otherPublishTime )
			return 0;
		else
			return 1;
	}
}
