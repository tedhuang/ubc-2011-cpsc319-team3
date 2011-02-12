package classes;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconn {
	private Connection DB_Conn = null;

	public DBconn() throws Exception
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			DB_Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jobzdroid",
			        "web", "somepw");
			
			System.out.println("Succesfully Connected");
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}	
	}
	
	public Connection getDBConnection()
	{
		return DB_Conn;
	}

}  