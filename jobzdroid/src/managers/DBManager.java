package managers;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import classes.DBconn;

public class DBManager {
	private Connection conn;	
	private ResultSet rs;
	private Statement stm;
		
	public DBManager()
	{
		try {
			conn = new DBconn().getDBConnection();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
}
