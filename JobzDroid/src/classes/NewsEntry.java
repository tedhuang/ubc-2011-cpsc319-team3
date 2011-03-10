package classes;

public class NewsEntry {
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
}
