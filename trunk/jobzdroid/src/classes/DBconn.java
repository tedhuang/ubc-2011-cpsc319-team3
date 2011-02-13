package classes;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBconn{
	private Connection conn = null;

	public DBconn() throws Exception{
		Class.forName("com.mysql.jdbc.Driver").newInstance();		
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jobzdroid", "web", "somepw");
	}
	
	public Connection getDBConnection(){
		return conn;
	}

}  